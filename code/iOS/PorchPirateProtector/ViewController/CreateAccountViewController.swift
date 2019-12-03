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
            initialViewModelHandler: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            },
            secureStorage: Keychain.shared,
            completion: weakify { strongSelf, viewModel in
                strongSelf.configureForViewModel(viewModel)
            })
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    deinit {
        self.presenter.onDestroy()
    }
    
    private func configureForViewModel(_ viewModel: CreateAccountPresenter.CreateAccountViewModel) {
        self.emailInput.errorText = viewModel.emailError
        self.passwordInput.errorText = viewModel.passwordError
        self.confirmPasswordInput.errorText = viewModel.confirmPasswordError
        self.createAccountButton.isEnabled = viewModel.submitButtonEnabled

        if let apiError = viewModel.apiErrorMessage {
            self.errorLabel.text = apiError
            self.errorLabel.isHidden = false
        } else {
            self.errorLabel.isHidden = true
        }
                
        if viewModel.indicatorAnimating {
            self.loadingSpinner.startAnimating()
        } else {
            self.loadingSpinner.stopAnimating()
        }
        
        if viewModel.accountCreated {
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
