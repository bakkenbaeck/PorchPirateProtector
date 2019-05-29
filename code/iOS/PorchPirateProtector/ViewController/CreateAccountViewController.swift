//
//  CreateAccountViewController.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/11/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import PPPShared
import UIKit

class CreateAccountViewController: UIViewController {
    enum CreateAccountSegue: String, Segue {
        case accountCreated
    }
    
    @IBOutlet private var emailInput: TextInputContainer!
    @IBOutlet private var passwordInput: TextInputContainer!
    @IBOutlet private var confirmPasswordInput: TextInputContainer!
    
    @IBOutlet private var createAccountButton: UIButton!
    @IBOutlet private var errorLabel: UILabel!
    @IBOutlet private var loadingSpinner: UIActivityIndicatorView!
    
    private lazy var presenter = CreateAccountPresenter(view: self, storage: Keychain.shared)
    
    @IBAction private func createAccount() {
        self.presenter.createAccount()
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.setNavigationBarHidden(false, animated: animated)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(true, animated: animated)
        super.viewWillDisappear(animated)
    }
}

extension CreateAccountViewController: UITextFieldDelegate {
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        switch textField {
        case self.emailInput.textInputView?.textField:
            self.presenter.validateEmail()
        case self.passwordInput.textInputView?.textField:
            self.presenter.validatePassword()
        case self.confirmPasswordInput.textInputView?.textField:
            self.presenter.validateConfirmPassword()
        default:
            assertionFailure("Unhandled text field: \(textField)")
        }
    }
}

extension CreateAccountViewController: CreateAccountView {
    func emailErrorUpdated(toString: String?) {
        self.emailInput.errorText = toString
    }
    
    func passwordErrorUpdated(toString: String?) {
        self.passwordInput.errorText = toString
    }
    
    func confirmPasswordErrorUpdated(toString: String?) {
        self.confirmPasswordInput.errorText = toString
    }
    
    func apiErrorUpdated(toString: String?) {
        self.errorLabel.text = toString
        self.errorLabel.isHidden = (toString == nil)
    }
    
    func setSubmitButtonEnabled(enabled: Bool) {
        self.createAccountButton.isEnabled = enabled
    }
    
    func accountSuccessfullyCreated() {
        self.perform(segue: CreateAccountSegue.accountCreated)
    }
    
    var email: String? {
        get {
            return self.emailInput.text
        }
        set(email) {
            self.emailInput.text = email
        }
    }
    
    var password: String? {
        get {
            return self.passwordInput.text
        }
        set(password) {
            self.passwordInput.text = password
        }
    }
    
    var confirmPassword: String? {
        get {
            return self.confirmPasswordInput.text
        }
        set(confirmPassword) {
            self.confirmPasswordInput.text = confirmPassword
        }
    }
    
    func startLoadingIndicator() {
        self.loadingSpinner.startAnimating()
    }
    
    func stopLoadingIndicator() {
        self.loadingSpinner.stopAnimating()
    }
}
