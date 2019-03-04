//
//  ViewController.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/4/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit
import PPPShared

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let label = UILabel(frame: CGRect(x: 0, y: 0, width: 300, height: 21))
        label.center = self.view.center
        label.textAlignment = .center
        label.font = label.font.withSize(25)
        label.text = CommonKt.createApplicationScreenMessage()
        view.addSubview(label)
    }
}

