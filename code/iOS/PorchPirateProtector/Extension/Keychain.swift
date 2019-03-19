//
//  Keychain.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/13/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import Foundation
import PPPShared

enum KeychainQueryKey {
    case account
    case `class`
    case matchLimit
    case returnAttributes
    case returnData
    case valueData
    case service
    
    var keyString: String {
        switch self {
        case .account:
            return kSecAttrAccount as String
        case .class:
            return kSecClass as String
        case .matchLimit:
            return kSecMatchLimit as String
        case .returnAttributes:
            return kSecReturnAttributes as String
        case .returnData:
            return kSecReturnData as String
        case .service:
            return kSecAttrService as String
        case .valueData:
            return kSecValueData as String
        }
    }
}

struct SingleItemQuery {
    
    let account: String
    let service: String
    
    var data: Data?
    var isForFetch = true
    
    init(account: String,
         service: String) {
        self.account = account
        self.service = service
    }
    
    mutating func addData(_ data: Data) {
        self.data = data
    }
    
    mutating func setIsForFetch(_ isForFetch: Bool) {
        self.isForFetch = isForFetch
    }
    
    private var baseDict: [KeychainQueryKey: AnyObject] {
        var dict = [KeychainQueryKey: AnyObject]()
        dict[.account] = self.account as AnyObject
        dict[.service] = self.service as AnyObject
        dict[.class] = kSecClassGenericPassword
        
        return dict
    }
    
    private func convertToStringKeys(dict: [KeychainQueryKey: AnyObject]) -> [String: AnyObject] {
        var finalDict = [String: AnyObject]()
        for (key, value) in dict {
            finalDict[key.keyString] = value
        }

        return finalDict
    }
    
    var removeDict: [String: AnyObject] {
        return self.convertToStringKeys(dict: self.baseDict)
    }
 
    var queryDict: [String : AnyObject] {
        var dict = self.baseDict
        dict[.matchLimit] = kSecMatchLimitOne
        dict[.returnAttributes] = kCFBooleanTrue
        dict[.returnData] = kCFBooleanTrue

        return self.convertToStringKeys(dict: dict)
    }
    
    var addDict: [String: AnyObject] {
        var dict = self.baseDict
        if let data = self.data {
            dict[.valueData] = data as AnyObject
        }
        
        return self.convertToStringKeys(dict: dict)
    }
    
    var updateDict: [String: AnyObject] {
        return self.convertToStringKeys(dict: self.baseDict)
    }
}

enum KeychainError: Error {
    case unexpectedPasswordData
    case unhandledError(error: NSError)
}

// Must be objc or it freaks out when kotlin tries to access it
@objc class Keychain: NSObject, SecureStorage {
    
    static let shared = Keychain()
    private override init() {
        super.init()
    }
    
    private func storeValue(_ value: String, for query: SingleItemQuery) throws {
        let password = try self.value(for: query)
        let passwordExists = (password != nil)
        
        let valueAsData = value.data(using: .utf8)!
        
        let status: OSStatus
        if passwordExists {
            // Update!
            var attributesToUpdate = [String : AnyObject]()
            attributesToUpdate[KeychainQueryKey.valueData.keyString] = valueAsData as AnyObject?
            status = SecItemUpdate(query.updateDict as CFDictionary, attributesToUpdate as CFDictionary)
        } else {
            // Create!
            var queryCopy = query
            queryCopy.addData(valueAsData)
            status = SecItemAdd(queryCopy.addDict as CFDictionary, nil)
        }
        
        switch status {
        case noErr:
            // Success!
            break
        default:
            let error = NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: nil)
            throw KeychainError.unhandledError(error: error)
        }
    }
    
    private func removeValue(for query: SingleItemQuery) throws {
        let status = SecItemDelete(query.removeDict as CFDictionary)
        
        switch status {
        case noErr,
             errSecItemNotFound:
            // Either one of these is an appropriate value.
            break
        default:
            let error = NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: nil)
            throw KeychainError.unhandledError(error: error)
        }
    }
    
    private func value(for query: SingleItemQuery) throws -> String? {
        var fetchedData: AnyObject?
        let status = withUnsafeMutablePointer(to: &fetchedData) {
            SecItemCopyMatching(query.queryDict as CFDictionary, UnsafeMutablePointer($0))
        }

        switch status {
        case errSecItemNotFound:
            // Nothing is stored for this account
            return nil
        case noErr:
            // The operation should have succeded
            break
        default:
            // Something else went wrong
            let error = NSError(domain: NSOSStatusErrorDomain, code: Int(status), userInfo: nil)
            throw KeychainError.unhandledError(error: error)
        }
        
        guard
            let itemDictionary = fetchedData as? [String: AnyObject],
            let passwordData = itemDictionary[KeychainQueryKey.valueData.keyString] as? Data,
            let password = String(data: passwordData, encoding: .utf8) else {
                throw KeychainError.unexpectedPasswordData
        }
        
        return password
    }
    
    private let service = "no.bakkenbaeck.porchpirateprotector.keychain"
    private let account = "user_token"
    
    func storeTokenString(token: String) {
        let query = SingleItemQuery(account: self.account, service: self.service)
        do {
            try self.storeValue(token, for: query)
        } catch {
            NSLog("Error storing token string: \(error)")
        }
    }
    
    func clearTokenString() {
        let query = SingleItemQuery(account: self.account, service: self.service)
        do {
            try self.removeValue(for: query)
        } catch {
            NSLog("Error removing token: \(error)")
        }
    }
    
    func fetchTokenString() -> String? {
        let query = SingleItemQuery(account: self.account, service: self.service)
        do {
            let token = try self.value(for: query)
            return token
        } catch {
            NSLog("Error fetching token string: \(error)")
            return nil
        }
    }
}
