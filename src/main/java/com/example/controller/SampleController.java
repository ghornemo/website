package com.example.controller;

import org.springframework.social.*;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.connect.GoogleOAuth2Template;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.social.twitter.*;
import org.springframework.jdbc.core.*;
import javax.sql.DataSource;

import com.example.model.Cart;
import com.example.model.Comment;
import com.example.model.GoogleProfile;
import com.example.model.Item;
import com.example.model.Post;
import com.example.model.Profile;
import com.example.model.Rating;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@EnableAutoConfiguration
@Repository
public class SampleController {
	
	public Profile getProfile(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
	        	if(profile != null) {
	        		return profile;
	        	}
	}
        
		return null;
        
	}
	
    public double averageCost(ArrayList<Cart> orders) {
		double cost = 0;
		int size = orders.size();
			for(Cart order : orders) {
				cost += order.total();
			}
			if(size > 0)
				cost /= size;
		return cost;
    }
    
    public double totalCost(ArrayList<Cart> orders) {
    		double total = 0;
    			for(Cart order : orders) {
    				total += order.total();
    			}
    		return total;
    }


	
	@ModelAttribute("totalSpent")
	public double totalCashSpent(HttpServletRequest request){
        	Profile profile = getProfile(request);
        	double total = 0;
        	if(profile != null) {
        		String email = profile.getEmail();
        		ArrayList<Cart> orders = getOrders(email);
        			for(Cart order : orders) {
        				total += order.total();
        			}
        	}
		return total;
	}
	
	@ModelAttribute("averageSpent")
	public double averageCashSpent(HttpServletRequest request){
        	Profile profile = getProfile(request);
        	double total = 0;
        	if(profile != null) {
        		String email = profile.getEmail();
        		ArrayList<Cart> orders = getOrders(email);
        			for(Cart order : orders) {
        				total += order.total();
        			}
                	if(orders.size() > 0)
                		total /= orders.size();
        	}
		return total;
	}
	
	@ModelAttribute("previousOrders")
	public ArrayList<Cart> getOrderHistory(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		ArrayList<Cart> orders = new ArrayList<>();
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        	if(profile != null) {
        		String email = profile.getEmail();
        		orders = getOrders(email);
        		session.setAttribute("orderHistory", orders);
        	}
        	
        }
	    return orders;
	}
	
	@ModelAttribute("email")
	public String getEmail(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		String email = "none";
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        	if(profile != null)
        email = profile.getEmail();
        	
        }
	    return email;
	}
	
	@ModelAttribute("memberSince")
	public Date getJoinDate(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		Date joinDate = new Date(0);
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        	if(profile != null) {
        		if(profile.getMemberSince() != null)
        joinDate = profile.getMemberSince();
        		if(joinDate != null)
        		return joinDate;
        	}
        }
	    return joinDate;
	}
	
	@ModelAttribute("fullName")
	public String getFullName(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		String name = "none";
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        	if(profile != null)
        name = profile.getName();
        	
        }
	    return name;
	}
	
	@ModelAttribute("followedPosts")
	public ArrayList<Post> getPosts(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		String email = "none";
		ArrayList<Post> posts = new ArrayList<>();
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        	if(profile != null) {
        email = profile.getEmail();
        	
        	posts = (ArrayList<Post>) findAllPosts(email);
        	}
        	}
	    return posts;
	}
	
	@ModelAttribute("test")
	public String getPerson(HttpServletRequest request){
		/*HttpSession session = request.getSession(false);
		String name = "none";
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        name = profile.getName();
        	
        }*/
	    return "test";
	}
	
	@ModelAttribute("loggedIn")
	public boolean loggedIn(HttpServletRequest request){
		HttpSession session = request.getSession(false);
        if(session != null) {
        return true;
        }
	    return false;
	}
	
	public ArrayList<Cart> todayOrders = new ArrayList<Cart>();
	
	//@Autowired
	//public FoodRatings FoodRatings;
	
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    JdbcTemplate jdbcTemplate;
	
	/*private Twitter twitter;
	
    private ConnectionRepository connectionRepository;

    @Inject
    public SampleController(Twitter twitter, ConnectionRepository connectionRepository) {
        this.twitter = twitter;
        this.connectionRepository = connectionRepository;
    }*/
    
    @RequestMapping("Ratings") 
    String input(Model model, @RequestParam("itemName") String itemName) {
    		model.addAttribute("itemName", itemName);
    		model.addAttribute("reviews", new FoodRatings(getRatings(itemName)));
    		return "Ratings";
    }
    
    @PostMapping("submitRating")
    String rating(Model model, HttpServletRequest request, @RequestParam("itemName") String itemName , @RequestParam("title") String title, @RequestParam("score") short score, @RequestParam("comment") String comment) {
        HttpSession session = request.getSession(false);
        String header = "";
        String inner = "";
        if(session != null) {
        		Profile profile = (Profile)session.getAttribute("profile");
        		String email = profile.getEmail();
        		Rating rating = new Rating();
	    		String reviewer = profile.getName();
	    		rating.setScore(score);
	    		rating.setComment(comment);
	    		rating.setEmail(email);
	    		rating.setItemName(itemName);
	    		rating.setTitle(title);
	    		rating.setReviewer(reviewer);
	    		rating.print();
	    		insertRating(rating);
	    		header = "Thank you for your review";
	    		inner = "Thank you for using your time to write a review. Writing a review helps ";
	    		inner += "our staff to ensure optimal food quality.";
        }else {
	    		header = "Rating Not Submitted";
	    		inner = "There was a problem submitting your review, please try again.";
        }
    		model.addAttribute("header", header);
    		model.addAttribute("body", inner);
    		return "template";
    }
    
    @PostMapping("repeatOrder")
    String repeat(Model model, HttpServletRequest request, @RequestParam("orderid") int Id) {
    	Cart order = new Cart();
        HttpSession session = request.getSession(false);
        ArrayList<Cart> orders = new ArrayList<>();
        if(session != null) {
        		Profile profile = (Profile)session.getAttribute("profile");
        		String email = profile.getEmail();
        		orders = (ArrayList<Cart>) session.getAttribute("orderHistory");
        		if(orders == null) {
        			orders = getOrders(email);
        		}
    		}
        Cart tmp = findOrder(orders, Id);
        if(tmp != null)
        		order = tmp;
        session.setAttribute("cart", order);
        return "redirect:/store";
        }
    
    @PostMapping("OrderDetails")
    String details(Model model, HttpServletRequest request, @RequestParam("orderid") int Id) {
    	Cart order = new Cart();
        HttpSession session = request.getSession(false);
        ArrayList<Cart> orders = new ArrayList<>();
        if(session != null) {
        		Profile profile = (Profile)session.getAttribute("profile");
        		String email = profile.getEmail();
        		orders = (ArrayList<Cart>) session.getAttribute("orderHistory");
        		if(orders == null) {
        			orders = getOrders(email);
        		}
    			model.addAttribute("totalCost", totalCost(orders));
    			model.addAttribute("averageCost", averageCost(orders));
    		}
        Cart tmp = findOrder(orders, Id);
        if(tmp != null)
        		order = tmp;
    	model.addAttribute("order", order);
    		return "OrderDetails";
    }
    @RequestMapping("/additem")
    String add(Model model, HttpServletRequest request, @RequestParam("name") String name, @RequestParam("price") String price) {

        HttpSession session = request.getSession(false);
        if(session != null) {
        	Cart cart = (Cart) session.getAttribute("cart");
        	Item item = new Item(name, Float.parseFloat(price));
        	cart.add(item);
        	session.setAttribute("cart", cart);
        	
        }else{
    		String header = "Please sign in";
    		String inner = "Before you can begin making an order, please log in";
    		model.addAttribute("header", header);
    		model.addAttribute("body", inner);
    		return "template";
        }
    	return "redirect:/store";
        }
    @RequestMapping("/confirmOrder")
    	String add(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String header = "";
        String inner = "";
        if(session != null) {
        	Cart cart = (Cart) session.getAttribute("cart");
        	double total = cart.total();
        	if(total <= 0) {
        		header = "You must first build an order";
        		inner = "Your cart seems to be empty, please add an item to submit your order";
        	}else{
        		todayOrders.add(cart);
        		Profile profile = (Profile) session.getAttribute("profile");
        		String email = profile.getEmail();
        		insertOrder(cart, email);
        	//cart = new cart();
        	session.setAttribute("cart", new Cart());
        	int orderCount = todayOrders.size();
		header = "Thank you for your order";
		inner = "Your order has been accepted, and\n";
		inner += "is now being prepared. Your order will be ready in\n";
		inner += " approximately 20 minutes.";
		inner += "Your order #: "+orderCount;
        	}
        }else{
        	header = "Whoops,";
        	inner = "You don't appear to be signed on. Please sign in\n";
        	inner += "So you can easily create your pizza order.";
        }
		model.addAttribute("header", header);
		model.addAttribute("body", inner);
		return "template";
    }
    @RequestMapping("/clearCart")
    String clear(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if(session != null) {
        	Cart cart = (Cart) session.getAttribute("cart");
        	cart.clear();
        	session.setAttribute("cart", cart);
        	
        }
    	return "redirect:/store";
        }
    private String round(double number) {
    	String solution = ""+number;
    	int end = solution.indexOf(".");
    	solution = solution.substring(0,end+3);
    	return solution;
    }
    @RequestMapping("/store")
    String home(Model model, HttpServletRequest request) {
    		model.addAttribute("FoodRatings", new FoodRatings(getRatings()));
        HttpSession session = request.getSession(false);
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        	Cart cart = (Cart) session.getAttribute("cart");
        	double total = cart.total();
        	if(total <= 0) {
            	model.addAttribute("name", profile.getName());
            	model.addAttribute("total", "$0.00");
            	model.addAttribute("HST", "HST: $0.00");
            	model.addAttribute("grandtotal", "Grand total: $0.00");
        	}else{
        	double HST = total*0.15;
        	double grandtotal = total+HST;
        	model.addAttribute("items", cart.items);
        	model.addAttribute("total", "Subtotal: $"+round(total));
        	model.addAttribute("HST", "HST: $"+round(HST));
        	model.addAttribute("grandtotal", "Grand total: $"+round(grandtotal));
        	model.addAttribute("name", profile.getName());
        	}
        }else {
        	model.addAttribute("name", "Not signed in");
        	model.addAttribute("total", "Total: $0.00");
        	model.addAttribute("HST", "HST: $0.00");
        	model.addAttribute("grandtotal", "Grand total: $0.00");
        }
        return "shop";
    }
    @RequestMapping("/authenticate")
    String authenticate(HttpServletRequest request) {
    	HttpSession session = request.getSession();
    	String token = request.getParameter("idtoken");
    	session.setAttribute("idToken", token);
    	session.setAttribute("cart", new Cart());
    	addDetails(session);
    	System.out.println("This is the server receiving the token. ID is");
    	System.out.println(token);
    	return "redirect:";
    }
    @RequestMapping("/addcomment")
    String addComment(@RequestParam("body") String body, @RequestParam("user") String user, @RequestParam("id") int id, Model model) {
    	if(user == "") {
    		String header = "Please sign in";
    		String inner = "You must be logged in to post a comment";
    		model.addAttribute("header", header);
    		model.addAttribute("body", inner);
    		return "template";
    	}
    	if(body == "") {
    		String header = "No comment received";
    		String inner = "Your comment seems to be empty, please try again.";
    		model.addAttribute("header", header);
    		model.addAttribute("body", inner);
    		return "template";
    	}
    	this.jdbcTemplate.update(
    	        "insert into comment (name, comment, id) values ('"+user+"', '"+body+"', '"+id+"');");
    	return "redirect:";
    }
    @RequestMapping("/comments")
    String comments(HttpServletRequest request, @RequestParam("user") String user, @RequestParam("date") String date, @RequestParam("body") String body, @RequestParam("id") int id, Model model) {
    	List<Comment> comments = findAllComments(id);
    	HttpSession session = request.getSession(false);
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        model.addAttribute("name", profile.getName());
        }
        model.addAttribute("size",comments.size());
    	model.addAttribute("comments",comments);
    	model.addAttribute("date",date);
    	model.addAttribute("user",user);
    	model.addAttribute("body",body);
    	model.addAttribute("id",id);
    	return "comment";
    }
    @RequestMapping("/post")
    String share(HttpServletRequest request, @RequestParam("user") String user, @RequestParam("body") String body, @RequestParam("date") String time) {
    	HttpSession session = request.getSession(false);
		String email = "";
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        	if(profile != null)
        email = profile.getEmail();
        }
    	this.jdbcTemplate.update(
    	        "insert into posts (text, name, time, pinner) values ('"+body+"', '"+user+"', '"+time+"', '"+email+"');");
        
        String response = "user: "+user;
        response += "\n body: "+body;
        
    	return "redirect:";
        }
    
    @RequestMapping("/")
    String readTweets(Model model, HttpServletRequest request) {

    	//Add trump tweets to the view
    	String consumerKey = "Iub87O9Z8J8MAVoLKqH01GCNZ"; // The application's consumer key
    	String consumerSecret = "XpeyafpCowjQ2CUhl1t84G73li1VECMcdJYV0kLzc0cznsMhcz"; // The application's consumer secret
    	Twitter twitter = new TwitterTemplate(consumerKey, consumerSecret);
    	List<Tweet> tweets = twitter.timelineOperations().getUserTimeline("realDonaldTrump");
    	List<Tweet> TrudeauTweets = twitter.timelineOperations().getUserTimeline("JustinTrudeau");
        model.addAttribute("tweets", tweets);
        model.addAttribute("trudeauTweets", TrudeauTweets);
        
        List<String> pictureURLs = new ArrayList<>();
        for(Tweet t : tweets) {
        	pictureURLs.add(t.getProfileImageUrl());
        }
        model.addAttribute("pictures", pictureURLs);
        //Add all posts to the view
        List<Post> posts = findAllPosts();
        //posts.get(0).
        HttpSession session = request.getSession(false);
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        	if(profile != null)
        model.addAttribute("name", profile.getName());
        }else
        	model.addAttribute("name", "Not signed in");
        model.addAttribute("posts", posts);
        model.addAttribute("size", posts.size());
        model.addAttribute("tweetsize", tweets.size()+TrudeauTweets.size());
    	return "index";
    }
    
    public void addDetails(HttpSession session) {
    	String token = (String)session.getAttribute("idToken");
    	try {
			URL url = new URL("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token="+token);
			Gson gson = new Gson();
	        BufferedReader in = new BufferedReader(
	                new InputStreamReader(url.openStream()));

	                String inputLine;
	                String result = "";
	                while ((inputLine = in.readLine()) != null)
	                    result += inputLine;
	                    System.out.println("result json:");
	                    System.out.println(result);
	                in.close();
	                 GoogleProfile profile = gson.fromJson(result, GoogleProfile.class);
	                 session.setAttribute("profile", profile.toProfile());
	                //Resulting JSON object is now in result.
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Something seems to have went terribly wrong!");
			e.printStackTrace();
		}
    	}
    
    public List<Post> findAllPosts() {
        return this.jdbcTemplate.query( "select * from posts", new ActorMapper());
    }
    
    public List<Post> findAllPosts(String email) {
        return this.jdbcTemplate.query( "select * from posts where pinner = '"+email+"'", new ActorMapper());
    }
    
    public List<Comment> findAllComments(int id) {
        return this.jdbcTemplate.query( "select * from comment where id = "+id, new CommentMapper());
    }
    
    public List<Item> findAllItems(String email) {
        return (List<Item>) this.jdbcTemplate.query( "select * from (item natural join orders) where email = '"+email+"'", new ItemMapper());
    }
    
    public void insertOrder(Cart order, String email) {
    		
        Integer ID = this.jdbcTemplate.query("insert into orders (email) values ('"+email+"') RETURNING orderid", new OrderMapper()).get(0);
        
        for(Item item : order.items) {
        	String sql = "insert into item (orderId, name, quantity, price) values ('"+ID+"', '"+item.getName()+"', '"+item.getQuantity()+"', '"+item.getPrice()+"');";
        this.jdbcTemplate.update(sql);
        }
    }
    
    public ArrayList<Cart> convertItemsToOrders(ArrayList<Item> items) {
    		ArrayList<Cart> orders = new ArrayList<>();
    		Cart order = new Cart();
    		int current = -1;//index of current order...
    		for(Item item : items) {
    			
    			if(current != item.getOrderId()) {//Setting up new order...
    				if(order.items.size() > 0)
    					orders.add(order);
    				order = new Cart();
    				order.setID(item.getOrderId());
    				current = order.getID();
    			}
    			order.add(item);
    		}
    		if(order.items.size() > 0)
    		orders.add(order);
    		return orders;
    }
    
public ArrayList<Cart> getOrders(String email) {
	ArrayList<Item> items = (ArrayList<Item>) findAllItems(email);
	return convertItemsToOrders(items);
}

final class RatingMapper implements RowMapper<Rating> {

    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
    		Rating rating = new Rating();
    		//score, email, comment, reviewer, itemName
    		rating.setScore(rs.getShort("score"));
    		rating.setEmail(rs.getString("email"));
    		rating.setComment(rs.getString("comment"));
    		rating.setReviewer(rs.getString("reviewer"));
    		rating.setItemName(rs.getString("itemName"));
    		rating.setTitle(rs.getString("title"));
    		rating.setTime(rs.getDate("date"));
        return rating;
    }
}
    
final class ItemMapper implements RowMapper<Item> {

    public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
        /*Comment comment = new Comment();
        comment.setComment(rs.getString("comment"));
        comment.setName(rs.getString("name"));
        comment.setId(rs.getInt("id"));
        comment.setDate(rs.getDate("time"));*/
    		Item item = new Item();
    		item.orderId = rs.getInt("orderid");
    		item.setName(rs.getString("name"));
    		item.setPrice(rs.getFloat("price"));
    		item.setQuantity(rs.getInt("quantity"));
        return item;
    }
}
    
    final class OrderMapper implements RowMapper<Integer> {

        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            /*Comment comment = new Comment();
            comment.setComment(rs.getString("comment"));
            comment.setName(rs.getString("name"));
            comment.setId(rs.getInt("id"));
            comment.setDate(rs.getDate("time"));*/
            return rs.getInt("orderId");
        }
    }
	
    final class CommentMapper implements RowMapper<Comment> {

        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment();
            comment.setComment(rs.getString("comment"));
            comment.setName(rs.getString("name"));
            comment.setId(rs.getInt("id"));
            comment.setDate(rs.getDate("time"));
            return comment;
        }
    }

    final class ActorMapper implements RowMapper<Post> {

        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            Post post = new Post();
            post.setBody(rs.getString("text"));
            post.setUser(rs.getString("name"));
            post.setId(rs.getInt("id"));
            post.setDate(rs.getDate("time"));
            return post;
        }
    }
    
    public Cart findOrder(List<Cart> orders, int ID) {
    		for(Cart order : orders) {
    			if(order.ID == ID)
    				return order;
    		}
    		return null;
    }
    
    private class FoodRating {
    		ArrayList<Rating> ratings;
    		String itemName;
    		public FoodRating(String itemName) {
    			this.itemName = itemName;
    		}
    }
    
    public void insertRating(Rating rating) {
			short score = rating.score;
			String email = rating.getEmail();
			String comment = rating.getComment();
			String reviewer = rating.getReviewer();
			String itemName = rating.getItemName();
			String title = rating.getTitle();
			String sql = "insert into rating (score, email, title, comment, reviewer, itemName) values ("+score+", '"+email+"', '"+title+"', '"+comment+"', '"+reviewer+"', '"+itemName+"')";
			jdbcTemplate.update(sql);
		}
    
    private class FoodRatings {
		public ArrayList<Rating> ratings;
		public FoodRatings(List<Rating> list) {
			this.ratings = (ArrayList<Rating>) list;
			System.out.println("# ratings: "+this.ratings.size());
		}
		public ArrayList<Rating> getRatings(String itemName) {
			if(ratings == null) return null;
			ArrayList<Rating> list = new ArrayList<>();
			for(Rating rating : ratings) {
				if(rating.getItemName().equalsIgnoreCase(itemName))
					list.add(rating);
			}
			return list;
		}
		public String average(String itemName) {
			double average = 0;
			double counter = 0;
			if(ratings == null) return "0 reviews";
			for(Rating rating : ratings) {
				if(rating.getItemName().equalsIgnoreCase(itemName)) {
					average += rating.score;
					counter++;
				}
			}
			if(counter > 0) 
				average = (double) (average / counter);
			else
				return "0 Reviews";
			String rating = average+"";
			rating = rating.substring(0, 3);
			return rating+" stars";
		}
}
    
    public List<Rating> getRatings() {
    		String sql = "select * from rating";
    		 return this.jdbcTemplate.query(sql, new RatingMapper());
    }
    public List<Rating> getRatings(String name) {
		String sql = "select * from rating where itemName = '"+name+"'";
		 return this.jdbcTemplate.query(sql, new RatingMapper());
}

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }
}
