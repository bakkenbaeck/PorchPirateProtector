//
//  TextInputView.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/11/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

@IBDesignable
class TextInputContainer: NibContainer {
    
    override var contentViewType: NibLoadableView.Type {
        return TextInputView.self
    }
    
    var textInputView: TextInputView? {
        return self.contentView as? TextInputView
    }
    
    @IBOutlet var textFieldDelegate: UITextFieldDelegate? {
        didSet {
            self.textInputView?.textField.delegate = self.textFieldDelegate
        }
    }
    
    @IBInspectable
    var text: String? {
        get {
            return self.textInputView?.textField?.text
        }
        set {
            self.textInputView?.textField?.text = newValue
        }
    }
    
    @IBInspectable
    var errorText: String? {
        didSet {
            self.textInputView?.errorLabel?.text = self.errorText
        }
    }
    
    @IBInspectable
    var fieldName: String? {
        didSet {
            self.textInputView?.fieldLabel?.text = self.fieldName
            self.textInputView?.textField?.placeholder = self.fieldName
        }
    }
    
    @IBInspectable
    var highlightColor: UIColor = .black {
        didSet {
            self.textInputView?.configureForHighlightColor(self.highlightColor)
        }
    }
    
    @IBInspectable
    var useSecureTextEntry: Bool = false {
        didSet {
            self.textInputView?.textField?.isSecureTextEntry = self.useSecureTextEntry
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.textInputView?.configureForHighlightColor(self.highlightColor)
    }
    
    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        if self.fieldName.isNullOrEmpty {
            self.fieldName = "Test"
        }
        
        if self.errorText.isNullOrEmpty {
            self.errorText = "Test Error"
        }
    }
}

class TextInputView: UIView {
    
    @IBOutlet private(set) var textField: UITextField!
    @IBOutlet private(set) var errorLabel: UILabel!
    @IBOutlet private(set) var fieldLabel: UILabel!
    @IBOutlet private(set) var underline: UIView!
    
    private let margin: CGFloat = 4
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.errorLabel.text = nil
        self.textField.text = nil
        self.textField.placeholder = nil
    }
    
    func configureForHighlightColor(_ color: UIColor) {
        self.errorLabel?.textColor = color
        self.underline?.backgroundColor = color
        self.fieldLabel?.textColor = color
    }
    
    override var intrinsicContentSize: CGSize {
        guard self.fieldLabel != nil else {
            return .zero
        }
        
        let height = self.fieldLabel.intrinsicContentSize.height
            + self.margin
            + self.textField.intrinsicContentSize.height
            + self.margin
            + self.underline.intrinsicContentSize.height
            + self.margin
            + self.errorLabel.intrinsicContentSize.height
        
        return CGSize(width: UIView.noIntrinsicMetric,
                      height: height)
    }
}

extension TextInputView: NibLoadable {}


