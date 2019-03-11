//
//  NibContainer.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/11/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

typealias NibLoadableView = NibLoadable & UIView

class NibContainer: UIView {
    
    open var contentViewType: NibLoadableView.Type {
        fatalError("Subclasses must override")
    }
    
    var contentView: UIView?
    
    func setupContentView() {
        let view = self.contentViewType.fromNib()
        view.frame = bounds
        view.autoresizingMask =
            [.flexibleWidth, .flexibleHeight]
        addSubview(view)
        self.contentView = view
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.setupContentView()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        self.setupContentView()
    }
    
    override func prepareForInterfaceBuilder() {
        super.prepareForInterfaceBuilder()
        self.contentView?.prepareForInterfaceBuilder()
    }
    
    override var intrinsicContentSize: CGSize {
        return self.contentView?.intrinsicContentSize ?? .zero
    }
}

