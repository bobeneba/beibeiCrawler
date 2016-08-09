package com.yks.beibei.crawler

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import com.yks.beibei.ebean.CategoryModel

import spider.man.crawler.SpiderFetch
import spider.man.fork.ForkJoin
import spider.man.io.DBIO
import spider.man.io.db
import spider.man.misc.HttpSupport
import spider.man.parse.JsoupSupport
import com.yks.beibei.ebean.CategoryFactoryModel

object CategoryFetch extends App with SpiderFetch with DBIO with HttpSupport with JsoupSupport with ForkJoin {
  var ebean = db("db_beibei_category_url", 30)
  var ebean1 = db("db_beibei_category_factory", 30)
  
  var header = createHeaders.append("Upgrade-Insecure-Requests", "1")
    .append("Connection", "keep-alive").append("host", "d.beibei.com")
    .append("cookie", "tem-au=ul198023202074126; pgv_pvi=8238448640; pgv_si=s9374867456; _jzqx=1.1470104164.1470104164.1.jzqsr=1%2E1%2E1%2E2|jzqct=/ac_portal/20160516120646/pc%2Ehtml.-; _jzqckmp=1; Hm_lvt_2f9c2192a3db990ee9e63707ba0ba1ed=1470104163,1470104660; Hm_lpvt_2f9c2192a3db990ee9e63707ba0ba1ed=1470106082; _ga=GA1.2.1756257119.1470104164; CNZZDATA1000288582=4871352-1470100453-http%253A%252F%252Fwww.beibei.com%252F%7C1470105853; _qzja=1.496763377.1470104198738.1470104198738.1470104198739.1470106075366.1470106082934.0.0.0.37.1; _qzjb=1.1470104198739.37.0.0.0; _qzjc=1; _qzjto=37.1.0; _jzqa=1.3555718536969001500.1470104164.1470104164.1470104164.1; _jzqc=1; _jzqb=1.39.10.1470104164.1")
    
  var sqlRow = fromDB("db_beibei_category_url", "select id, category_url from category where status =1 ")
  
  sqlRow.foreach(record => {
    var list: List[CategoryModel] = List()
    
    val time1 = new Timestamp(new Date().getTime())
    var data1 = new CategoryFactoryModel()
    data1.setStart_time(time1)
    var baseurl = record.getString("category_url")
    var id = record.getString("id").toInt
    data1.setId(id)
    data1.setStatus(2)
    ebean1.update(data1)
    
    var listUrls = page(baseurl, x => x, checkBoundary, urlFactory)(x => false)("utf-8")(header)()
    listUrls.foreach(listUrl => {

      println("[info]::fetch url:" + listUrl)
      val producturl = fetch(listUrl, x => x, parseDocument _)(x => false)("utf-8")(header)()
      println("list size" + list.size)
      list = list ::: (producturl)

    })
    
    
   

    println("[list size]::" + list.size)
    list.foreach(db => {
      ebean.save(db)
    })
    
    val time = new Timestamp(new Date().getTime())
    data1.setEnd_time(time)
    data1.setId(id)
    data1.setStatus(3)
    ebean1.update(data1)

  })

  def urlFactory(h: String, num: Int): String = {
    var src = h
    var dom_base = src.substring(0, src.indexOf("item/") + 5) + "-"
    var dom_base_num = src.substring(src.indexOf("cat=") + 4, src.length())
    dom_base = dom_base + dom_base_num + "---hot-" + num + ".html"
    dom_base
  }
  
  
  def checkBoundary(h: String): Int = {

    var document = parseHtml(h)
    val dom_base = document.getElementsByAttributeValue("class", "pagination").get(0).children()
    val temp = dom_base.size() - 2
    var num = dom_base.get(temp).html().toInt
    println("num" + num)
    num
  }

  def parseDocument(h: String): List[CategoryModel] = {
    /**
     * *
     * top100 分类下的产品 ebean 字段对应
     * 排名，item,标题，产品链接，图片链接，售价价格，评论，评论分数，
     * 跟卖数量，分类名，分类id,抓取时间，评论链接,原始售价,跟卖,跟卖价格
     */

    var list = List[CategoryModel]()

    var title = ""
    var product_url = ""
    var image_url = ""
    var price = ""
    var discount = ""
    var catch_time: Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("0000-00-00 00:00:00")

    var doc = parseHtml(h)
    val dom_product = doc.getElementsByAttributeValue("class", "m-items clearfix view-ItemList z-ancient").get(0).children()
    var product_num = dom_product.size()

    for (i <- 0 to product_num - 1) {

      var data = new CategoryModel()
      product_url = dom_product.get(i).getElementsByTag("a").attr("href").toString()
      title = dom_product.get(i).getElementsByTag("img").attr("title").toString()
      image_url = dom_product.get(i).getElementsByTag("img").attr("data-src").toString()
      discount = dom_product.get(i).getElementsByAttributeValue("class", "discount").html().trim()
      discount = discount.substring(0, discount.indexOf("件"))
      price = dom_product.get(i).getElementsByAttributeValue("class", "price price-int").html()
      catch_time = new Timestamp(new Date().getTime())

      data.setCatch_time(catch_time)
      data.setImage_url(image_url)
      data.setTitle(title)
      data.setProduct_url(product_url)
      data.setPrice(price)
      data.setDiscount(discount)

      list = list.::(data)

    }

    println("document list" + list.size)

    list
  }

}