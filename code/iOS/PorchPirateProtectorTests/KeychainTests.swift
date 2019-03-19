//
//  KeychainTests.swift
//  PorchPirateProtectorTests
//
//  Created by Ellen Shapiro on 3/19/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import Foundation
import XCTest
@testable import PorchPirateProtector

class KeychainTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        
        Keychain.shared.clearTokenString()
    }
    
    func testStoringRetrievingAndClearingPasswordInKeychain() {
        let testToken = "I am a TEST TOKEN"
        Keychain.shared.storeTokenString(token: testToken)
        
        let retrievedToken = Keychain.shared.fetchTokenString()
        XCTAssertNotNil(retrievedToken)
        XCTAssertEqual(retrievedToken, testToken)
        
        Keychain.shared.clearTokenString()
        
        let reretrievedToken = Keychain.shared.fetchTokenString()
        XCTAssertNil(reretrievedToken)
    }
}
