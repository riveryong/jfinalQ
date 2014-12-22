package com.uikoo9.fore.controller;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.uikoo9.manage.blog.model.BlogArticleModel;
import com.uikoo9.manage.blog.model.BlogTypeModel;
import com.uikoo9.util.QStringUtil;
import com.uikoo9.util.jfinal.QControllerUrl;

@QControllerUrl("/blog")
public class BlogController extends Controller{
	
	/**
	 * 跳转到博客首页 
	 */
	public void list(){
		setAttr("blogTypes", BlogTypeModel.dao.findAllByCache());
		
		try {
			Integer typeId = getParaToInt(0);
			if(typeId != null){
				setAttr("blogTypeId", typeId);
				setAttr("blogs", Db.find("select * from t_blog_article tba where tba.blog_type_id=? order by cdate desc", typeId));
			}else{
				setAttr("blogs", BlogArticleModel.dao.find("select * from t_blog_article tba order by cdate desc"));
			}
			
			render("/WEB-INF/view/fore/blog/blog-list.ftl");
		} catch (Exception e) {
			redirect("/blog/list");
		}
	}
	
	/**
	 * 跳转到编辑博客页面
	 */
	public void edit(){
		setAttr("blogTypes", BlogTypeModel.dao.findAllByCache());
		if(getParaToInt(0) != null){
			setAttr("blog", BlogArticleModel.dao.findById(getParaToInt(0)));
		}
		
		render("/WEB-INF/view/fore/blog/blog-edit.ftl");
	}
	
	/**
	 * 跳转到博客详情页面
	 */
	public void detail(){
		try {
			String blogCode = getPara();
			if(QStringUtil.notEmpty(blogCode)){
				BlogArticleModel blog = BlogArticleModel.dao.findFirst("select * from t_blog_article where blog_article_code=?", blogCode);
				blog.set("blog_article_read_times", ((Integer)blog.get("blog_article_read_times") + 1)).update();
				
				setAttr("blog", blog);
				setAttr("prevBlog", getBlog("prev", blog));
				setAttr("nextBlog", getBlog("next", blog));
				
				render("/WEB-INF/view/fore/blog/blog-detail.ftl");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		redirect("/blog/list");
	}
	private BlogArticleModel getBlog(String type, BlogArticleModel blog){
		BlogArticleModel theBlog = null;
		try {
			String sql = null;
			if("prev".equals(type)) sql = "select * from t_blog_article where id = (select max(id) from t_blog_article where id<?)";
			if("next".equals(type)) sql = "select * from t_blog_article where id = (select min(id) from t_blog_article where id>?)";
			theBlog = BlogArticleModel.dao.findFirst(sql, blog.getInt("id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return theBlog != null ? theBlog : new BlogArticleModel().set("id", 0).set("blog_article_title", "没有了~").set("blog_article_code", "null");
	}
	
}
