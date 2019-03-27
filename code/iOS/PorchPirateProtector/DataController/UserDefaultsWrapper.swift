//
//  UserDefaultsWrapper.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/26/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import Foundation
import PPPShared

@objc class UserDefaultsWrapper: NSObject {
    
    static let shared = UserDefaultsWrapper()
    private override init() {
        super.init()
    }
    
    private lazy var defaults = UserDefaults.standard
    
    enum DefaultsKey: String {
        case ipAddresses = "no.bakkenbaeck.porchpirateprotector.ipAddresses"
    }
    
    private func store<T>(value: T, for key: DefaultsKey) {
        self.defaults.setValue(value, forKey: key.rawValue)
    }
    
    private func removeValue(for key: DefaultsKey) {
        self.defaults.removeObject(forKey: key.rawValue)
    }
    
    private func retrieveValue<T>(for key: DefaultsKey) -> T? {
        return self.defaults.value(forKey: key.rawValue) as? T
    }
    
    private func retrieveBoolean(for key: DefaultsKey) -> Bool {
        guard let storedBool: Bool = self.retrieveValue(for: key) else {
            return false
        }

        return storedBool
    }
    
    private func storeStringArray(_ array: [String], for key: DefaultsKey) {
        self.store(value: array, for: key)
    }
    
    private func loadStringArray(for key: DefaultsKey) -> [String]? {
        return self.retrieveValue(for: key)
    }
}

extension UserDefaultsWrapper: InsecureStorage {
    func storeIPAddresses(list: [String]) {
        self.storeStringArray(list, for: .ipAddresses)
    }
    
    func loadIPAddresses() -> [String]? {
        return self.loadStringArray(for: .ipAddresses)
    }
    
    func removeIPAddress(address: String) {
        guard
            var addresses = self.loadIPAddresses(),
            let index = addresses.firstIndex(of: address) else {
                // Nothing to remove
                return
        }
        
        addresses.remove(at: index)
        self.storeIPAddresses(list: addresses)
    }
    
    func clearIPAddresses() {
        self.removeValue(for: .ipAddresses)

    }
    
    
    
}
