package com.yks.beibei.ebean

import scala.beans.BeanProperty
import javax.persistence.{ Transient, Lob, Column, Table, Entity, Id, GeneratedValue, GenerationType }

import java.sql.Timestamp
import java.util.Formatter.DateTime
import java.util.Date

@Entity
@Table(name = "category_product")
class CategoryModel {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @BeanProperty
  @Column(name = "id", nullable = false, length = 20)
  var id = 0;
    
  @BeanProperty
  @Column(name = "title", nullable = false)
  var title = "";
   
  @BeanProperty
  @Column(name = "price", nullable = false)
  var price = "";

  @BeanProperty
  @Column(name = "discount", nullable = false)
  var discount = "";
  
  @BeanProperty
  @Column(name = "product_url", nullable = false)
  var product_url = "";

  @BeanProperty
  @Column(name = "image_url", nullable = false)
  var image_url = "";
  
  @BeanProperty
  @Column(name = "catch_time", nullable = false)
  var catch_time:Date =_
 
  
}