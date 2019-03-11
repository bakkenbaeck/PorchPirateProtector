//
//  LoginViewController.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/11/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit
import PPPShared

class LoginViewController: UIViewController {
    
    @IBOutlet private var emailTextInput: TextInputContainer!
    @IBOutlet private var passwordTextInput: TextInputContainer!
    @IBOutlet private var loginButton: UIButton!
    @IBOutlet private var apiErrorLabel: UILabel!
    @IBOutlet private var activityIndicator: UIActivityIndicatorView!
    
    private lazy var presenter = LoginPresenter(view: self)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
    }
    
    @IBAction private func login() {
        self.presenter.login()
    }
}

extension LoginViewController: LoginView {
    func emailErrorUpdated(toString: String?) {
        self.emailTextInput.errorText = toString
    }
    
    func passwordErrorUpdated(toString: String?) {
        self.passwordTextInput.errorText = toString
    }
    
    func apiErrorUpdated(toString: String?) {
        self.apiErrorLabel.text = toString
    }
    
    func loginSucceeded() {
        NSLog("LOGIN SUCCEEDED")
    }
    
    var email: String? {
        get {
            return self.emailTextInput.text
        }
        set(email) {
            self.emailTextInput.text = email
        }
    }
    
    var password: String? {
        get {
            return self.passwordTextInput.text
        }
        set(password) {
            self.passwordTextInput.text = password
        }
    }
    
    func startLoadingIndicator() {
        self.activityIndicator.startAnimating()
    }
    
    func stopLoadingIndicator() {
        self.activityIndicator.stopAnimating()
    }
}
