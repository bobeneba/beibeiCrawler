package com.yks.beibei.ebean

import scala.beans.BeanProperty
import javax.persistence.{ Transient, Lob, Column, Table, Entity, Id, GeneratedValue, GenerationType }
import java.util.Date
import java.sql.Timestamp

@Entity
@Table(name = "category")
class CategoryFactoryModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @BeanProperty
  @Column(name = "id", nullable = false, length = 20)
  var id = 0;

  @BeanProperty
  @Transient
  @Column(name = "category_url",nullable = false)
  var category_url = "";
   
    
  @BeanProperty
  @Column(name = "status",nullable = false)
  var status = 0;
   
  @BeanProperty
  @Column(name = "start_time", nullable = false)
  var start_time:Date = _;

  @BeanProperty
  @Column(name = "end_time", nullable = false)
  var end_time:Date = _;
  
}