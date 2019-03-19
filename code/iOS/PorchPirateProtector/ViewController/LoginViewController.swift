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
    enum LoginSegue: String, Segue {
        case loginSucceeded
    }
    
    @IBOutlet private var emailTextInput: TextInputContainer!
    @IBOutlet private var passwordTextInput: TextInputContainer!
    @IBOutlet private var loginButton: UIButton!
    @IBOutlet private var apiErrorLabel: UILabel!
    @IBOutlet private var activityIndicator: UIActivityIndicatorView!
    
    private lazy var presenter = LoginPresenter(view: self, storage: Keychain.shared)
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.setNavigationBarHidden(false, animated: animated)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(true, animated: animated)
        super.viewWillDisappear(animated)
    }
    
    @IBAction private func login() {
        self.presenter.login()
    }
}

// MARK: - UITextFieldDelegate

extension LoginViewController: UITextFieldDelegate {
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        switch textField {
        case self.emailTextInput.textInputView?.textField:
            self.presenter.validateEmail()
        case self.passwordTextInput.textInputView?.textField:
            self.presenter.validatePassword()
        default:
            assertionFailure("Unhandled text field: \(textField)")
        }
    }
}

// MARK: - LoginView

extension LoginViewController: LoginView {
    
    func setSubmitButtonEnabled(enabled: Bool) {
        self.loginButton.isEnabled = enabled
    }
    
    func emailErrorUpdated(toString: String?) {
        self.emailTextInput.errorText = toString
    }
    
    func passwordErrorUpdated(toString: String?) {
        self.passwordTextInput.errorText = toString
    }
    
    func apiErrorUpdated(toString: String?) {
        self.apiErrorLabel.text = toString
        self.apiErrorLabel.isHidden = (toString == nil)
    }
    
    func loginSucceeded() {
        self.perform(segue: LoginSegue.loginSucceeded)
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
