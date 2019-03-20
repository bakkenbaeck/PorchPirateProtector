//
//  UIViewController+Error.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/20/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

extension UIViewController {
    
    func showErrorBanner(with message: String, animated: Bool = true, for showDuration: TimeInterval = 5) {
        self.showBanner(with: message,
                        animated: animated,
                        for: showDuration,
                        backgroundColor: .red)
    }
        
    func showBanner(with message: String,
                    animated: Bool = true,
                    for showDuration: TimeInterval = 3,
                    backgroundColor: UIColor?) {
        let banner = BannerView.fromNib()
        banner.translatesAutoresizingMaskIntoConstraints = false
        if let bgColor = backgroundColor {
            banner.backgroundColor = bgColor
        } // else use default
        
        banner.messageLabel.text = message
        self.view.addSubview(banner)
        
        let topConstraint = banner.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor)
        
        self.view.addConstraints([
            topConstraint,
            banner.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            banner.trailingAnchor.constraint(equalTo: view.trailingAnchor),
        ])
        
        view.layoutIfNeeded()
        
        topConstraint.constant = -banner.frame.height
        view.layoutIfNeeded()
        
        let animationDuration: TimeInterval = animated ? 0.3 : 0.0
        
        topConstraint.constant = 0
        UIView.animate(
            withDuration: animationDuration,
            delay: 0,
            options: [.curveEaseOut],
            animations: {
                self.view.layoutIfNeeded()
            },
            completion: { [weak self] _ in
                guard let self = self else { return }
                topConstraint.constant = -banner.frame.height
                UIView.animate(
                    withDuration: animationDuration,
                    delay: showDuration,
                    options: [.curveEaseIn],
                    animations: {
                        self.view.layoutIfNeeded()
                    },
                    completion: { _ in
                        banner.removeFromSuperview()
                    })
            })
    }
}
