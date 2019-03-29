//
//  Buttons.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/29/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import PPPShared
import UIKit

// NOTE: To get proper behaivor for the selected background
// image, make sure to set the `buttonType` to Custom in
// Interface Builder or use the `ClassName(type: .custom)`
// convenience initializer
@IBDesignable
class StyleButton: UIButton {
    
    var style: ButtonStyle! {
        didSet {
            self.titleLabel?.font = self.style.textStyle.toUIFont()
            self.setTitleColor(self.style.textStyle.fontColor.toUIColor(), for: .normal)
            let regularBackground = UIImage.pixelFrom(color: self.style.backgroundColor.toUIColor())
            self.setBackgroundImage(regularBackground, for: .normal)
            
            let selectedBackground = UIImage.pixelFrom(color: self.style.selectedBackgroundColor.toUIColor())
            self.setBackgroundImage(selectedBackground, for: .selected)
            self.setBackgroundImage(selectedBackground, for: .highlighted)
                        
            self.clipsToBounds = true
            self.layer.cornerRadius = self.style.cornerRadius.points.toCGFloat
            self.invalidateIntrinsicContentSize()
        }
    }
    
    func commonInit() {
        // Sublcasses should override
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.commonInit()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.commonInit()
    }
    
    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        self.commonInit()
    }
    
    override var intrinsicContentSize: CGSize {
        let labelSize = self.titleLabel?.intrinsicContentSize ?? .zero
        let marginToAdd = self.style.minimumInnerMargin.points.toCGFloat * 2
        let width = labelSize.width + marginToAdd
        let height = labelSize.height + marginToAdd
        return CGSize(width: width, height: height)
    }
}

class DefaultStyleButton: StyleButton {
    
    override func commonInit() {
        super.commonInit()
        self.style = DefaultButtonStyle()
    }
}
