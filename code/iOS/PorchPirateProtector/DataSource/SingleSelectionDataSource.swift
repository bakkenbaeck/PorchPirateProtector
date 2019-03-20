//
//  SingleSelectionDataSource.swift
//  PorchPirateProtector
//
//  Created by Ellen Shapiro on 3/20/19.
//  Copyright © 2019 Bakken & Bæck. All rights reserved.
//

import UIKit

open class SingleSelectionDataSource<ItemType, CellType: UITableViewCell>: NSObject, UITableViewDataSource, UITableViewDelegate {
    
    private var items: [ItemType]
    private weak var tableView: UITableView?
    
    init(tableView: UITableView, items: [ItemType]) {
        self.items = items
        self.tableView = tableView

        super.init()
        
        tableView.delegate = self
        tableView.dataSource = self
    }
    
    func item(at indexPath: IndexPath) -> ItemType {
        return self.items[indexPath.row]
    }
    
    func reloadData() {
        self.tableView?.reloadData()
    }
    
    func updateItems(to items: [ItemType]) {
        self.items = items
        self.tableView?.reloadData()
    }
    
    open func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }
    
    open func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: CellType.identifier, for: indexPath) as? CellType else {
            fatalError("Couldn't get cell with identifier \(CellType.identifier)")
        }
        
        let item = self.item(at: indexPath)
        self.configure(cell: cell, for: item)
        
        return cell
    }
    
    open func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    open func configure(cell: CellType, for item: ItemType) {
        assertionFailure("Subclasses must override")
    }
}
