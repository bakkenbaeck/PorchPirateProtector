//
//  Keychain.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/13/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import Foundation
import PPPShared

// Must be objc or it freaks out when kotlin tries to access it
@objc class Keychain: NSObject, SecureStorage {
    
    static let shared = Keychain()
    private override init() {
        super.init()
    }
    
    //TODO: Actually use keychain
    private var token: String?
    
    func storeTokenString(token: String) {
        self.token = token
    }
    
    func clearTokenString() {
        self.token = nil
    }
    
    func fetchTokenString() -> String? {
        return self.token
    }
    
    
}
