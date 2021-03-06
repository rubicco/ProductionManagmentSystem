package com.ubs.ubs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ubs.ubs.model.Goodin;
import com.ubs.ubs.model.GoodinView;
import com.ubs.ubs.model.Inventory;
import com.ubs.ubs.model.InventoryId;
import com.ubs.ubs.model.Product;
import com.ubs.ubs.model.RecentTransaction;
import com.ubs.ubs.model.ReturnGiView;
import com.ubs.ubs.model.ReturnGoView;
import com.ubs.ubs.model.Warehouse;
import com.ubs.ubs.repository.GoodinRepository;
import com.ubs.ubs.repository.GoodinViewRepository;
import com.ubs.ubs.repository.InventoryRepository;
import com.ubs.ubs.repository.ProductRepository;
import com.ubs.ubs.repository.RecentTransactionsRepository;
import com.ubs.ubs.repository.ReturnGiViewRepository;
import com.ubs.ubs.repository.ReturnGoViewRepository;
import com.ubs.ubs.repository.WarehouseRepository;


@RestController
@RequestMapping(value = "/good_in")
public class GoodinController {

	@Autowired
	ProductRepository productRepository;
	@Autowired
	WarehouseRepository warehouseRepository;
	@Autowired
	InventoryRepository inventoryRepository;
	@Autowired
	GoodinRepository goodinRepository;
	@Autowired
	GoodinViewRepository goodinViewRepository;
	@Autowired
	RecentTransactionsRepository recentRepository;
	@Autowired
	ReturnGiViewRepository returnGiViewRepository;

	
	@CrossOrigin(allowCredentials="true")
	@PostMapping(value = "/insertGoods")
	public @ResponseBody ResponseEntity<Goodin> setProduct(@RequestBody Goodin g) {
		Product p = productRepository.findById(g.getP_id());
		Warehouse w = warehouseRepository.findById(g.getW_id());
		System.out.println(org.hibernate.Version.getVersionString());
		if(productRepository.existsById(p.getId()) && warehouseRepository.existsById(w.getId())) {						
			Inventory i = new Inventory();
			i.setProduct(p);
			i.setWarehouse(w);
			if(inventoryRepository.existsById(new InventoryId(p,w))) {
				Inventory i2 = inventoryRepository.getOne(new InventoryId(p,w));
				i.setQty(i2.getQty() + g.getQty());		
			}
			else
				i.setQty(g.getQty());
			inventoryRepository.save(i);
			goodinRepository.save(g);
			RecentTransaction rt = new RecentTransaction(g.getType(), g.getId());
			recentRepository.save(rt);
			return new ResponseEntity<Goodin>(g,HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
			
	}
	
	@CrossOrigin(allowCredentials="true")
	@GetMapping(value = "/insertLogs")
	public @ResponseBody ResponseEntity<List<GoodinView>> showLogs(){
		if(goodinViewRepository.count() == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			
		}else {
			return new ResponseEntity<List<GoodinView>>(goodinViewRepository.findAll(),HttpStatus.OK);
		}
	}
	@CrossOrigin(allowCredentials="true")
	@GetMapping(value = "/returnGi")
	public @ResponseBody List<ReturnGiView> goodOutReturn(){
		return returnGiViewRepository.findAll();
	}
	

}
