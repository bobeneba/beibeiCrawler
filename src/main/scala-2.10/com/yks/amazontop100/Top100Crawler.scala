package com.yks.amazontop100

import scala.collection.mutable.ArrayBuffer

import javax.persistence.Entity
import javax.persistence.Table
import spider.man.crawler.SpiderFetch
import spider.man.fork.ForkJoin
import spider.man.io.DBIO
import spider.man.io.db
import spider.man.misc.HttpSupport
import spider.man.parse.JsoupSupport
import scala.collection.mutable.ListBuffer

object Top100Crawler extends App with SpiderFetch with DBIO with HttpSupport with JsoupSupport with ForkJoin {
  var ebean = db("db_amazon_top100", 30)
  var array: ArrayBuffer[String] = ArrayBuffer()
      var list: List[Amazontop100Model] = List()

  var header = createHeaders.append("Upgrade-Insecure-Requests", "1")
    .append("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36")
    .append("Connection", "keep-alive").append("host", "www.amazon.com")
    .append("cookie", "x-wl-uid=1kSKiFJJTxy1mwDqHWzPD0tZH3WIdbcLAsEqmr+T1Pt8zK4prcefbTPH4WRD6jE4C1CBYOnhHEO8=; session-token=fEllkuapTZ2iw9xIuN3NnUj2LMJMSMAeXafvGbtstWmzVeRrjtE202YMUVjr+qNMnu9F7moGVXauNjcxaJky6PIoxoXgtfpAJd7/aVkYzPz9lmthWacqwrByRAWjgUa5nFOnfhxgQ3kCoIM+uYMtKF5OwTY6G1UR5gkB8h1CQQbUYNGKAxxXDdTu4Nmco4koxjM2BjgWbWiSRbsEb9mnxOnfzZ4GPEVPws4hfiuR9XJX/gHzXTKws+j4/NX7MoGZ; ubid-main=187-0257602-6218960; session-id-time=2082787201l; session-id=159-7770749-9169400; csm-hit=19DFNSYGCJE5T8R32CZS+s-19DFNSYGCJE5T8R32CZS|1468825087119")
  var sqlRow = fromDB("db_amazon_top100", "select category_id ,category_top100_url from amazon_top100_url limit 0,19400")

  //  sqlRow.foreach(x=>{
  //    println(x)
  //    fromDB("db_amazon_top100", "update amazon_top100_url SET status = 1 where category_id = '"+x.getString("category_id")+"'")
  //UPDATE persondata SET age=age*2, age=age+1;
  //  
  //  })

  sqlRow.foreach(record => {
     var list: List[Amazontop100Model] = List()

    var baseurl = record.getString("category_top100_url")
    var tempurl = record.getString("category_top100_url").substring(0, baseurl.length())
    var hh = fetch(tempurl, x => x, parseDocument _)(x => false)("utf-8")(header)()

    //   for (i <- 1 to 5) {
    // http://www.amazon.com/Best-Sellers-Home-Improvement-Hardware-Fasteners/zgbs/hi/511332/ref=zg_bs_511332_pg_3?_encoding=UTF8&pg=3&ajax=1

    var header2 = createHeaders.append("X-Requested-With", "XMLHttpRequest").append("Referer", tempurl)
      .append("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36").append("Connection", "keep-alive").append("host", "www.amazon.com").append("Cookie", "x-wl-uid=1kSKiFJJTxy1mwDqHWzPD0tZH3WIdbcLAsEqmr+T1Pt8zK4prcefbTPH4WRD6jE4C1CBYOnhHEO8=; session-token=fEllkuapTZ2iw9xIuN3NnUj2LMJMSMAeXafvGbtstWmzVeRrjtE202YMUVjr+qNMnu9F7moGVXauNjcxaJky6PIoxoXgtfpAJd7/aVkYzPz9lmthWacqwrByRAWjgUa5nFOnfhxgQ3kCoIM+uYMtKF5OwTY6G1UR5gkB8h1CQQbUYNGKAxxXDdTu4Nmco4koxjM2BjgWbWiSRbsEb9mnxOnfzZ4GPEVPws4hfiuR9XJX/gHzXTKws+j4/NX7MoGZ; csm-hit=0PXTBBQTVHDZYFXEXKVA+s-0PXTBBQTVHDZYFXEXKVA|1468824285369; ubid-main=187-0257602-6218960; session-id-time=2082787201l; session-id=159-7770749-9169400")

    //  var hh = fetchX(urlFactory, x=>x, parseDocument _)(hasRejected)(howToContinue)("utf-8")(header)(1)
    list = list.:::(hh)
    //    list.foreach(db => {
    //      ebean.save(db)
    //    })
  if(hh.size>1){
    
 
    var amazon_ebean = hh.last
    array += amazon_ebean.ajax_2
    array += amazon_ebean.ajax_3
    array += amazon_ebean.ajax_4
    array += amazon_ebean.ajax_5
    //  println("ajax"+amazon_ebean.ajax_2)
    array.foreach { tempurl =>
      {

        println("[info]:: ajax url factory~~~~~"+tempurl)
        var ajax1ebean = fetch(tempurl, x => x, parseDocumentajax1 _)(x => false)("utf-8")(header2)()
        list = list ::: (ajax1ebean)
        val ajax2_url = tempurl + "&isAboveTheFold=0"
        var ajax2ebean = fetch(ajax2_url, x => x, parseDocumentajax2 _)(x => false)("utf-8")(header2)()
        list = list ::: (ajax2ebean)

      }
    }
    array.clear()
    println("list size"+list.size)
    list.foreach(db => {
//      println("mysql::::::"+db)
//      println(db.item)
      ebean.save(db)
    })
    
  //  println("ldkjflsjfl;djlsfdjklskjdf;lskdjf;lsfj")
//    list.foreach(x=>println(x.item))
  }
    //      childForkJoin[Amazontop100Model](hh, dr=>{
    //        
    //        
    //      })
    //   }
  })
  //  def howToContinue(fetchUrl: String) {
  ////    println("continueFetch start")
  ////    var hh = fetch(fetchUrl, x => x, parseDocument _)(x => false)("utf-8")(header)()
  ////        println("continueFetch end")
  ////    println("sleep1")
  ////      Thread.sleep(6000)
  ////       println("sleep2")
  //     println("屏蔽，在次抓取")
  //
  //  }
  // def continueFetch(fetchUrl: String) = ADSLUtils.reconnect(fetchUrl)

  //  def hasRejected(html:String):Boolean={
  //       println("判断是否屏蔽")
  //        var doc = parseHtml(html)
  //        var flag= doc.getElementById("zg_centerListWrapper")
  //        if(flag == null){
  //          true
  //        }else{
  //          false
  //        }
  //
  //   
  //  }
  def parseDocumentajax1(h: String): List[Amazontop100Model] = {
    println("[info]::amazon crawler ajax1 page start ~~~~~~~~~~~~~~~~~~~~~~")
    var list = List[Amazontop100Model]()
    var zg_rankNumber = 0
    var item = ""
    var title = ""
    var product_url = ""
    var image_url = ""
    var price = ""
    var review = ""
    var review_rate = ""
    var follow_seller = ""
    var category = ""
    var category_id = ""
    var catch_time: Long = 0
    var review_url = ""
    var price_original = ""
    var follow_seller_price = ""

    var doc = parseHtml(h)
    
    val url_base = doc.getElementsByAttributeValue("class", "zg_itemImmersion")
    if (url_base == null) {
      println("[Error]::fuck crawler: ajax1 抓取3个产品屏蔽")
    } else {
      for (i <- 0 to 2) {
       // println("begin ajax1")
        var forbase_url = url_base.get(i)
       //  println("begin ajax1")
          if(forbase_url.getElementsByClass("zg_rankNumber").html().size<1){
          zg_rankNumber =200
        }else{
                  zg_rankNumber = forbase_url.getElementsByClass("zg_rankNumber").html().replace(".", "").toInt

        }
         if(forbase_url.getElementsByClass("zg_itemImageImmersion").html().size <12){
           product_url = ""
           category_id = ""
         }else{
          product_url = forbase_url.getElementsByClass("zg_itemImageImmersion").get(0).getElementsByTag("a").attr("href").trim()
          item = product_url.substring(product_url.indexOf("/dp/")+4,product_url.indexOf("/ref="))

          var temp_category_id = product_url.substring(product_url.indexOf("ref=zg_bs_") + 10, product_url.length)
        category_id = temp_category_id.substring(0, temp_category_id.indexOf("_"))

         }
        image_url = forbase_url.getElementsByTag("img").attr("src").trim()
        title = forbase_url.getElementsByTag("img").attr("title").trim()
        // review_url =  forbase_url.getElementsByClass("a-link-normal a-text-normal").attr("href")
        review_url = forbase_url.getElementsByAttributeValue("class", "a-link-normal a-text-normal").attr("href")
    
         var size = forbase_url.getElementsByAttributeValue("class", "a-link-normal").html().size
         
        if (size <1) {
       //   println("review is null" + review)
          review = "0"
       //   println("reult"+forbase_url.getElementsByAttributeValue("class", "a-link-normal").html())
      
        } else {
          review = forbase_url.getElementsByAttributeValue("class", "a-link-normal").get(0).html().replace(",", "")
        }
     //   println("review" + review)
     //   println("1")
        if (forbase_url.getElementsByAttributeValue("class", "zg_price").html().size<12) {
          price = "0"
        } else {
      //    println("bug1")
          price = forbase_url.getElementsByAttributeValue("class", "zg_price").get(0).child(0).html().replace("$", "").replace(",", "")

        }
    //    println("6")
    //    println("fuck~~~~~~" + forbase_url.getElementsByAttributeValue("class", "zg_usedPrice"))

        if (forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").html().size <12) {
          follow_seller = "0"
        } else {
     //     println("lkdfjl;sj")
     //     println("koko"+forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").first().html())
          follow_seller = forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").first().getElementsByTag("a").html().replace("&nbsp;new", "").replace("used &amp; new", "").replace("&nbsp;used", "")

        }
     //   println("3")
//        if (forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").html().size <12) {
//          follow_seller_price = "0"
//        } else {
//          println("88"+forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").get(0).html())
//          println(forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").get(0).child(0).html())
//          follow_seller_price = forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").get(0).child(1).html().replace("$", "").replace(",", "")
//
//        }
     //   println("4")
        review_rate = forbase_url.getElementsByAttributeValue("class", "a-icon-alt").html()
        if (review_rate == "") {
        println("review is null")
        } else {
          review_rate = review_rate.substring(0, 3)

        }
        catch_time = System.currentTimeMillis().toLong

        if (follow_seller_price == "") {
         println("follow_seller_price is null" + follow_seller_price)
          follow_seller_price = "0"
        }
        if (price_original == "") {
        println("price_original is null" + price_original)
          price_original = "0"
        }
        if (follow_seller == "") {
          println("follow_seller is null" + follow_seller)
          follow_seller = "0"
        }
        if (price == "") {
          println("follow_seller is null" + price)
          price = "0"
        }
        if (review == "") {
          println("follow_seller is null" + price)
          review = "0"
        }

//        println("----------------------------")
//        println("[info]" + "catch_time=" + catch_time)
//        println("[info]::" + "zg_rankNumber=" + zg_rankNumber)
//        println("[info]::" + "item=" + item)
//        println("[info]::" + "title=" + title)
//        println("[info]::" + "product_url=" + product_url)
//        println("[info]::" + "image_url=" + image_url)
//        println("[info]::" + "price=" + price)
//        println("[info]::" + "review=" + review)
//        println("[info]::" + "review_rate=" + review_rate)
//        println("[info]::" + "follow_seller=" + follow_seller)
//        println("[info]::" + "category=" + category)
//        println("[info]::" + "category_id=" + category_id)
//        println("[info]::" + "catch_time=" + catch_time)
//        println("[info]::" + "review_url=" + review_url)
//        println("[info]::" + "price_original=" + price_original)
//        println("[info]::" + "follow_seller_price=" + follow_seller_price)

        var data = new Amazontop100Model()
        data.setTitle(title)
        data.setZg_rankNumber(zg_rankNumber)
        data.setImage_url(image_url)
        data.setCatch_time(catch_time)
        data.setCategory(category)
        data.setCategory_id(category_id)
        data.setFollow_seller(follow_seller.trim())
        data.setFollow_seller_price(follow_seller_price)
        data.setItem(item)
        data.setPrice(price)
        data.setPrice_original(price_original)
        data.setProduct_url(product_url)
        data.setReview(review)
        data.setReview_rate(review_rate)
        data.setReview_url(review_url)

        list = list.::(data)

      }

    }
    println("[info]::amazon crawler ajax1 page end ~~~~~~~~~~~~~~~~~~~~~~")
     println("ajax1 list"+list.size)
    list
  }

  def parseDocumentajax2(h: String): List[Amazontop100Model] = {
    println("[info]::amazon crawler ajax2 page start ~~~~~~~~~~~~~~~~~~~~~~")
    var list = List[Amazontop100Model]()
    var zg_rankNumber = 0
    var item = ""
    var title = ""
    var product_url = ""
    var image_url = ""
    var price = ""
    var review = ""
    var review_rate = ""
    var follow_seller = ""
    var category = ""
    var category_id = ""
    var catch_time: Long = 0
    var review_url = ""
    var price_original = ""
    var follow_seller_price = ""

    var doc = parseHtml(h)

    val url_base = doc.getElementsByAttributeValue("class", "zg_itemImmersion")
    if (url_base == null) {
      println("[Error]::fuck crawler: ajax1 抓取3个产品屏蔽")
    } else {
      for (i <- 0 to 16) {
  //       println("begin ajax2")
        var forbase_url = url_base.get(i)
  //      println("begin ajax2")
        if(forbase_url.getElementsByClass("zg_rankNumber").html().size<1){
          zg_rankNumber =200
        }else{
                  zg_rankNumber = forbase_url.getElementsByClass("zg_rankNumber").html().replace(".", "").toInt

        }

       if(forbase_url.getElementsByClass("zg_itemImageImmersion").html().size <12){
           product_url = ""
           category_id = ""
         }else{
          product_url = forbase_url.getElementsByClass("zg_itemImageImmersion").get(0).getElementsByTag("a").attr("href").trim()
         item = product_url.substring(product_url.indexOf("/dp/")+4,product_url.indexOf("/ref="))

          var temp_category_id = product_url.substring(product_url.indexOf("ref=zg_bs_") + 10, product_url.length)
        category_id = temp_category_id.substring(0, temp_category_id.indexOf("_"))

         }
        image_url = forbase_url.getElementsByTag("img").attr("src").trim()
        title = forbase_url.getElementsByTag("img").attr("title").trim()
        // review_url =  forbase_url.getElementsByClass("a-link-normal a-text-normal").attr("href")
        review_url = forbase_url.getElementsByAttributeValue("class", "a-link-normal a-text-normal").attr("href")
       
          
         
        var size = forbase_url.getElementsByAttributeValue("class", "a-link-normal").html().size
        if (size <1) {
          println("review is null" + review)
          review = "0"
        } else {
          review = forbase_url.getElementsByAttributeValue("class", "a-link-normal").get(0).html().replace(",", "")
        }
  //      println("review" + review)
 //       println("bug")
        if (forbase_url.getElementsByAttributeValue("class", "zg_price").html().size<12 ) {
          price = "0"
        } else {
  //        println("bug3"+forbase_url.getElementsByAttributeValue("class", "zg_price").html())
          price = forbase_url.getElementsByAttributeValue("class", "zg_price").get(0).child(0).html().replace("$", "").replace(",", "")

        }
 //       println("6")
  //      println("fuck~~~~~~" + forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").html())
        if (forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").html().size <12) {
          follow_seller = "0"
    //      println("null")
        } else {
 //         println("lll")
 //         println("hhahah"+forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").first().html())
          follow_seller = forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").first().getElementsByTag("a").html().replace("&nbsp;new", "").replace("used &amp; new", "").replace("&nbsp;used", "")

        }
 //       println("43")
//        if (forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").html().size <12) {
//          follow_seller_price = "0"
//        } else {
//          println("67")
//          println("56"+forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").get(0).html())
//          if()
//          follow_seller_price = forbase_url.getElementsByAttributeValue("class", "zg_usedPrice").get(0).child(1).html().replace("$", "").replace(",", "")
//
//        }
        review_rate = forbase_url.getElementsByAttributeValue("class", "a-icon-alt").html()
        if (review_rate == "") {
          println("review is null")
        } else {
          review_rate = review_rate.substring(0, 3)

        }
        catch_time = System.currentTimeMillis().toLong

        if (follow_seller_price == "") {
          println("follow_seller_price is null" + follow_seller_price)
          follow_seller_price = "0"
        }
        if (price_original == "") {
          println("price_original is null" + price_original)
          price_original = "0"
        }
        if (follow_seller == "") {
          println("follow_seller is null" + follow_seller)
          follow_seller = "0"
        }
        if (price == "") {
          println("follow_seller is null" + price)
          price = "0"
        }
        if (review == "") {
          println("follow_seller is null" + price)
          review = "0"
        }

//        println("----------------------------")
//        println("[info]" + "catch_time=" + catch_time)
//        println("[info]::" + "zg_rankNumber=" + zg_rankNumber)
//        println("[info]::" + "item=" + item)
//        println("[info]::" + "title=" + title)
//        println("[info]::" + "product_url=" + product_url)
//        println("[info]::" + "image_url=" + image_url)
//        println("[info]::" + "price=" + price)
//        println("[info]::" + "review=" + review)
//        println("[info]::" + "review_rate=" + review_rate)
//        println("[info]::" + "follow_seller=" + follow_seller)
//        println("[info]::" + "category=" + category)
//        println("[info]::" + "category_id=" + category_id)
//        println("[info]::" + "catch_time=" + catch_time)
//        println("[info]::" + "review_url=" + review_url)
//        println("[info]::" + "price_original=" + price_original)
//        println("[info]::" + "follow_seller_price=" + follow_seller_price)

        var data = new Amazontop100Model()
        data.setTitle(title)
        data.setZg_rankNumber(zg_rankNumber)
        data.setImage_url(image_url)
        data.setCatch_time(catch_time)
        data.setCategory(category)
        data.setCategory_id(category_id)
        data.setFollow_seller(follow_seller.trim())
        data.setFollow_seller_price(follow_seller_price)
        data.setItem(item)
        data.setPrice(price)
        data.setPrice_original(price_original)
        data.setProduct_url(product_url)
        data.setReview(review)
        data.setReview_rate(review_rate)
        data.setReview_url(review_url)

        list = list.::(data)

      }

    }

    println("[info]::amazon crawler ajax2 page end ~~~~~~~~~~~~~~~~~~~~~~")
    println("ajax2 list"+list.size)
    list
  }

  def parseDocument(h: String): List[Amazontop100Model] = {
    /**
     * *
     * top100 分类下的产品 ebean 字段对应
     * 排名，item,标题，产品链接，图片链接，售价价格，评论，评论分数，
     * 跟卖数量，分类名，分类id,抓取时间，评论链接,原始售价,跟卖,跟卖价格
     */
    var list = List[Amazontop100Model]()

    var zg_rankNumber = 0
    var item = ""
    var title = ""
    var product_url = ""
    var image_url = ""
    var price = ""
    var review = ""
    var review_rate = ""
    var follow_seller = ""
    var category = ""
    var category_id = ""
    var catch_time: Long = 0
    var review_url = ""
    var price_original = ""
    var follow_seller_price = ""
    var ajax_2 = ""
    var ajax_3 = ""
    var ajax_4 = ""
    var ajax_5 = ""

    var doc = parseHtml(h)
    /**
     * *
     * 分类名抓取
     */
    var categorydom = doc.getElementById("zg_listTitle")
    if (categorydom != null) {
      category = categorydom.children().get(0).text()
    } else {
      println("[Warning]:: fuck crawler: 类目名字屏蔽 ")
    }

    /**
     * *
     *  页面抓取基础dom
     */
    val baseZG = doc.getElementById("zg_centerListWrapper")
    if (baseZG == null) {
      println("[Error]::fuck crawler: 20个整个产品列表屏蔽")
    } else {
      val ajaz_dom = baseZG.getElementById("zg_paginationWrapper")
      if (ajaz_dom == null) {
        println("[Error]::fuck crawler: 分页ajax屏蔽")
      } else {
        val ajax_dom = ajaz_dom.getElementsByTag("a")
        ajax_2 = ajax_dom.get(1).attr("ajaxUrl").trim()
        ajax_3 = ajax_dom.get(2).attr("ajaxUrl").trim()
        ajax_4 = ajax_dom.get(3).attr("ajaxUrl").trim()
        ajax_5 = ajax_dom.get(4).attr("ajaxUrl").trim()
      }

      val baseZGArray = baseZG.children()
      /**
       * *
       * 计算抓取的dom元素个数
       */
      val num = baseZGArray.size()
      /**
       * *
       * 循环20个产品页面，去除js标签文件
       */
      for (i <- 0 to num - 4) {
        if (i != 0 && i != 4 && i != 5 && i != 6) {
          var temp = baseZGArray.get(i).getAllElements.size()
          var basedom = baseZGArray.get(i);
          println("[info]::amazon crawler first page start ~~~~~~~~~~~~~~~~~~~~~~")
          println("抓取" + i + "个产品")
          /**
           * *
           * 排名抓取
           */
          if (basedom.html().size <12) {
            println("[Warning]:: fuck crawler: 屏蔽产品排名 ")
            zg_rankNumber = 2000
          } else {
            zg_rankNumber = basedom.child(0).text().replace(".", "").toInt
            println("zg_rankNumber" + zg_rankNumber)

             println("aaa"+zg_rankNumber)
          }
          /**
           * *
           * 图片链接和产品链接，标题抓取，
           */
          var temp_base_url = basedom.child(1)
          var temp_image_url = temp_base_url.child(0)
          product_url = temp_image_url.getElementsByTag("a").attr("href").trim()
          if (product_url != "") {
               println(product_url)
          item = product_url.substring(product_url.indexOf("/dp/")+4,product_url.indexOf("/ref="))

            var temp_category_id = product_url.substring(product_url.indexOf("ref=zg_bs_") + 10, product_url.length)
              println("temp_category_id"+temp_category_id)
            category_id = temp_category_id.substring(0, temp_category_id.indexOf("_"))
           println(category_id)
          }
          image_url = temp_image_url.getElementsByTag("img").attr("src")
          title = temp_image_url.getElementsByTag("img").attr("title")
          /**
           * *
           * 评论数，评分，评论链接，item ,原始售价，售价，跟卖数抓取
           */

          var review_rate = temp_base_url.getElementsByAttributeValue("class", "a-icon-alt").html()
          if (review_rate == "") {
            println("review is null")
          } else {
            review_rate = review_rate.substring(0, 3)

          }

          if( temp_base_url.getElementsByAttributeValue("class", "a-link-normal") == null){
            review ="0"
          }else{
                      review = temp_base_url.getElementsByAttributeValue("class", "a-link-normal").html()
          review = review.replace(",", "")

          }
          review_url = temp_base_url.getElementsByAttributeValue("class", "a-link-normal a-text-normal").attr("href")
       
          // 
          price = temp_base_url.getElementsByAttributeValue("class", "zg_price").get(0).getElementsByAttributeValue("class", "price").html()
          price = price.replace("$", "")
          if (price.size > 7) {
            price = price.substring(0, price.indexOf("-"))
          }
          price_original = temp_base_url.getElementsByAttributeValue("class", "listprice").html()
          price_original = price_original.replace("$", "")
          price_original = price_original.replace(",", "")

          var temp_foller = temp_base_url.getElementsByAttributeValue("class", "zg_usedPrice")
          /**
           * *
           * 判断是否存在跟卖标签存在
           */
          if (temp_foller.html() != "") {

            follow_seller_price = temp_foller.get(0).getElementsByAttributeValue("class", "price").html()
            follow_seller = temp_foller.get(0).child(0).html()

            follow_seller_price = follow_seller_price.replace("$", "")
            follow_seller_price = follow_seller_price.replace(",", "")

            follow_seller = follow_seller.replace("&nbsp;new", "")
            follow_seller = follow_seller.replace("used &amp; new", "")
            follow_seller = follow_seller.replace("&nbsp;used", "")
            follow_seller = follow_seller.trim()

          }
          catch_time = System.currentTimeMillis().toLong

          if (follow_seller_price == "") {
            println("follow_seller_price is null" + follow_seller_price)
            follow_seller_price = "0"
          }
          if (price_original == "") {
            println("price_original is null" + price_original)
            price_original = "0"
          }
          if (follow_seller == "") {
            println("follow_seller is null" + follow_seller)
            follow_seller = "0"
          }
          if (price == "") {
            println("follow_seller is null" + price)
            price = "0"
          }
          if (review == "") {
            println("follow_seller is null" + price)
            review = "0"
          }

//          println("----------------------------")
//          println("[info]" + "catch_time=" + System.currentTimeMillis())
//          println("[info]::" + "zg_rankNumber=" + zg_rankNumber)
//          println("[info]::" + "item=" + item)
//          println("[info]::" + "title=" + title)
//          println("[info]::" + "product_url=" + product_url)
//          println("[info]::" + "image_url=" + image_url)
//          println("[info]::" + "price=" + price)
//          println("[info]::" + "review=" + review)
//          println("[info]::" + "review_rate=" + review_rate)
//          println("[info]::" + "follow_seller=" + follow_seller)
//          println("[info]::" + "category=" + category)
//          println("[info]::" + "category_id=" + category_id)
//          println("[info]::" + "catch_time=" + catch_time)
//          println("[info]::" + "review_url=" + review_url)
//          println("[info]::" + "price_original=" + price_original)
//          println("[info]::" + "follow_seller_price=" + follow_seller_price)
//          println("[info]::" + "ajax_2=" + ajax_2)
//          println("[info]::" + "ajax_3=" + ajax_3)
//          println("[info]::" + "ajax_4=" + ajax_4)
//          println("[info]::" + "ajax_5=" + ajax_5)
          var data = new Amazontop100Model()
          data.setTitle(title)
          data.setZg_rankNumber(zg_rankNumber)
          data.setImage_url(image_url)
          data.setCatch_time(catch_time)
          data.setCategory(category)
          data.setCategory_id(category_id)
          data.setFollow_seller(follow_seller.trim())
          data.setFollow_seller_price(follow_seller_price)
          data.setItem(item)
          data.setPrice(price)
          data.setPrice_original(price_original)
          data.setProduct_url(product_url)
          data.setReview(review)
          data.setReview_rate(review_rate)
          data.setReview_url(review_url)

          data.setAjax_2(ajax_2)
          data.setAjax_3(ajax_3)
          data.setAjax_4(ajax_4)
          data.setAjax_5(ajax_5)

          list = list.::(data)
          // data.set

          /**
           * *
           *
           */
          println("----------------------------")
          // println(basedom.html())
          println("[info]::amazon crawler first page end ~~~~~~~~~~~~~~~~~~~~~~")
        }
      }
    }

    // println(baseZGArray)
    //    var tt = doc.getElementsByAttributeValue("class", "pagnLink").get(0).getElementsByTag("a").attr("href")
    //    var rh = tt.substring(tt.indexOf("rh=") + 3, tt.indexOf("&page"))
    //    var qid = tt.substring(tt.indexOf("qid=") + 4, tt.indexOf("&spIA"))
    //    var spIA = tt.substring(tt.lastIndexOf("spIA=") + 5, tt.length())
    //
    //    var data = new Amazontop100Model()
    ////    data.setRh(rh)
    ////    data.setQid(qid)
    ////    data.setSpIA(spIA)
    // lsit = lsit.::(data)
    // list = list.::()

    println(list)
 println("document list"+list.size)

    list
  }

}
