//
//  Weakable.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 12/3/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import Foundation

// Ganked from Vincent Pradeilles: https://github.com/vincent-pradeilles/weakable-self/blob/master/Sources/Weakable.swift

public protocol Weakable: class { }

extension NSObject: Weakable { }

public extension Weakable {
    
    func weakify(_ code: @escaping (Self) -> Void) -> () -> Void {
        return { [weak self] in
            guard let self = self else { return }
            
            code(self)
        }
    }
    
    func weakify<A>(_ code: @escaping (Self, A) -> Void) -> (A) -> Void {
        return { [weak self] a in
            guard let self = self else { return }
            
            code(self, a)
        }
    }
}
