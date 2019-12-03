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
    
    private var email: String? {
        return self.emailTextInput.text
    }
    
    private var password: String? {
        return self.passwordTextInput.text
    }
    
    private lazy var presenter = LoginPresenter()
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    @IBAction private func login() {
        self.presenter.login(
            email: self.email,
            password: self.password,
            initialViewModelHandler: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            },
            secureStorage: Keychain.shared,
            completion: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            })
    }
    
    private func configureForViewModel(_ viewModel: LoginPresenter.LoginViewModel) {
        self.emailTextInput.errorText = viewModel.emailError
        self.passwordTextInput.errorText = viewModel.passwordError
        self.loginButton.isEnabled = viewModel.submitButtonEnabled
        
        if let apiError = viewModel.apiError {
            self.apiErrorLabel.text = apiError
            self.apiErrorLabel.isHidden = false
        } else {
            self.apiErrorLabel.isHidden = true
        }
        
        if viewModel.indicatorAnimating {
            self.activityIndicator.startAnimating()
        } else {
            self.activityIndicator.stopAnimating()
        }
        
        if viewModel.loginSucceeded {
            self.loginSucceeded()
        }
    }
    
    private func loginSucceeded() {
        self.perform(segue: LoginSegue.loginSucceeded)
    }
}

// MARK: - UITextFieldDelegate

extension LoginViewController: UITextFieldDelegate {
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        switch textField {
        case self.emailTextInput.textInputView?.textField:
            self.emailTextInput.errorText = self.presenter.validateEmail(email: self.email)
        case self.passwordTextInput.textInputView?.textField:
            self.passwordTextInput.errorText = self.presenter.validatePassword(password: self.password)
        default:
            assertionFailure("Unhandled text field: \(textField)")
        }
    }
}
