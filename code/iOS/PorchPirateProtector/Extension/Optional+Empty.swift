//
//  Optional+Empty.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/11/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import Foundation

extension Optional where Wrapped: Collection {
    
    var isNullOrEmpty: Bool {
        switch self {
        case .none:
            return true
        case .some(let collection):
            return collection.isEmpty
        }
    }
}
