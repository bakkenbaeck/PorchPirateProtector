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
    
    private var email: String? {
        self.emailInput.text
    }

    private var password: String? {
        self.passwordInput.text
    }
    
    private var confirmPassword: String? {
        self.confirmPasswordInput.text
    }
    
    private lazy var presenter = CreateAccountPresenter()
    
    @IBAction private func createAccount() {
        self.presenter.createAccount(
            email: self.email,
            password: self.password,
            confirmPassword: self.confirmPassword,
            initialViewStateHandler: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            },
            secureStorage: Keychain.shared,
            completion: weakify { strongSelf, viewState in
                strongSelf.configureForViewState(viewState)
            })
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    deinit {
        self.presenter.onDestroy()
    }
    
    private func configureForViewState(_ viewState: CreateAccountPresenter.CreateAccountViewState) {
        self.emailInput.errorText = viewState.emailError
        self.passwordInput.errorText = viewState.passwordError
        self.confirmPasswordInput.errorText = viewState.confirmPasswordError
        self.createAccountButton.isEnabled = viewState.submitButtonEnabled

        if let apiError = viewState.apiErrorMessage {
            self.errorLabel.text = apiError
            self.errorLabel.isHidden = false
        } else {
            self.errorLabel.isHidden = true
        }
                
        if viewState.indicatorAnimating {
            self.loadingSpinner.startAnimating()
        } else {
            self.loadingSpinner.stopAnimating()
        }
        
        if viewState.accountCreated {
            self.accountSuccessfullyCreated()
        }
    }
    
    private func accountSuccessfullyCreated() {
        self.perform(segue: CreateAccountSegue.accountCreated)
    }
}

extension CreateAccountViewController: UITextFieldDelegate {

    func textFieldDidEndEditing(_ textField: UITextField) {
        switch textField {
        case self.emailInput.textInputView?.textField:
            self.emailInput.errorText = self.presenter.validateEmail(email: self.email)
        case self.passwordInput.textInputView?.textField:
            self.passwordInput.errorText = self.presenter.validatePassword(password: self.password)
        case self.confirmPasswordInput.textInputView?.textField:
            self.confirmPasswordInput.errorText =  self.presenter.validateConfirmPassword(password: self.password, confirmPassword: self.confirmPassword)
        default:
            assertionFailure("Unhandled text field: \(textField)")
        }
    }
}
