package com.example;

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
import org.springframework.web.servlet.view.RedirectView;

@Controller
@EnableAutoConfiguration
@Repository
public class SampleController {
	
	public ArrayList<cart> todayOrders = new ArrayList<cart>();
	
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
    @RequestMapping("/additem")
    String add(Model model, HttpServletRequest request, @RequestParam("name") String name, @RequestParam("price") String price) {

        HttpSession session = request.getSession(false);
        if(session != null) {
        	cart cart = (cart) session.getAttribute("cart");
        	item item = new item(name, Float.parseFloat(price));
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
        	cart cart = (cart) session.getAttribute("cart");
        	double total = cart.total();
        	if(total <= 0) {
        		header = "You must first build an order";
        		inner = "Your cart seems to be empty, please add an item to submit your order";
        	}else{
        		todayOrders.add(cart);
        	//cart = new cart();
        	session.setAttribute("cart", new cart());
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
        	cart cart = (cart) session.getAttribute("cart");
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
        HttpSession session = request.getSession(false);
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        	cart cart = (cart) session.getAttribute("cart");
        	double total = cart.total();
        	if(total <= 0) {
            	model.addAttribute("name", profile.name);
            	model.addAttribute("total", "Total: $0.00");
            	model.addAttribute("HST", "HST: $0.00");
            	model.addAttribute("grandtotal", "Grand total: $0.00");
        	}else{
        	double HST = total*0.15;
        	double grandtotal = total+HST;
        	model.addAttribute("items", cart.items);
        	model.addAttribute("total", "Subtotal: $"+round(total));
        	model.addAttribute("HST", "HST: $"+round(HST));
        	model.addAttribute("grandtotal", "Grand total: $"+round(grandtotal));
        	model.addAttribute("name", profile.name);
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
    	session.setAttribute("cart", new cart());
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
    String comments(HttpServletRequest request, @RequestParam("user") String user, @RequestParam("body") String body, @RequestParam("id") int id, Model model) {
    	List<Comment> comments = findAllComments(id);
    	HttpSession session = request.getSession(false);
        if(session != null) {
        	Profile profile = (Profile) session.getAttribute("profile");
        model.addAttribute("name", profile.name);
        }
        model.addAttribute("size",comments.size());
    	model.addAttribute("comments",comments);
    	model.addAttribute("user",user);
    	model.addAttribute("body",body);
    	model.addAttribute("id",id);
    	return "comment";
    }
    @RequestMapping("/post")
    String share(@RequestParam("user") String user, @RequestParam("body") String body, @RequestParam("date") String time) {
    	this.jdbcTemplate.update(
    	        "insert into posts (text, name, time) values ('"+body+"', '"+user+"', '"+time+"');");
        
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
        model.addAttribute("name", profile.name);
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
	                 Profile profile = gson.fromJson(result, Profile.class);
	                 session.setAttribute("profile", profile);
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
    
    public List<Comment> findAllComments(int id) {
        return this.jdbcTemplate.query( "select * from comment where id = "+id, new CommentMapper());
    }
    
    
    
    class cart {
    	public ArrayList<item> items = new ArrayList<>();
    	public void add(item i) {
    		for(item item : items) {
    			if(item.name.equals(i.name)) {
    				item.quantity++;
    				return;
    			}
    		}
    		i.quantity = 1;
    		items.add(i);
    	}
    	public void clear() {
    		items = new ArrayList<>();
    	}
    	public float total() {
    		float price = 0;
    		for(item item : items)
    			price += item.price*item.quantity;
    		return price;
    	}
    	
    }
    
    class item {
    	String name;
    	float price;
    	int quantity;
    	public item(String name, float price) {
    		this.name = name;
    		this.price = price;
    	}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public float getPrice() {
			return price;
		}
		public void setPrice(float price) {
			this.price = price;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
    	
    }
    
    class Profile {
    	 public String email;
    	 public String email_verified;
    	 public String name;
    	 public String picture;
    	 public String given_name;
    	 public String family_name;
    	 public String locale;
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public String getEmail_verified() {
			return email_verified;
		}
		public void setEmail_verified(String email_verified) {
			this.email_verified = email_verified;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPicture() {
			return picture;
		}
		public void setPicture(String picture) {
			this.picture = picture;
		}
		public String getGiven_name() {
			return given_name;
		}
		public void setGiven_name(String given_name) {
			this.given_name = given_name;
		}
		public String getFamily_name() {
			return family_name;
		}
		public void setFamily_name(String family_name) {
			this.family_name = family_name;
		}
		public String getLocale() {
			return locale;
		}
		public void setLocale(String locale) {
			this.locale = locale;
		}
    	 
    }
    
    class Post {
    	public String body;
    	public String user;
    	public int Id;
    	public Date date;
    	public int getId() {
			return Id;
		}
		public void setId(int id) {
			Id = id;
		}
		public Post() {
    		
    	}
    	public Post(String body, String user, Date date) {
    		this.body = body;
    		this.user = user;
    		this.date = date;
    	}
    	
    	public String getBody() {
			return body;
		}
		public String getUser() {
			return user;
		}
		public Date getDate() {
			return date;
		}
		public void setBody(String data) {
    		body = data;
    	}
    	public void setUser(String data) {
    		user = data;
    	}

    	public void setDate(Date data) {
    		date = data;
    	}
    }
    
   class Comment {
	   public int id;
	   public Date date;
	   public String name;
	   public String comment;
	   
	   public Comment(int id, Date date, String name, String comment) {
		   this.id = id;
		   this.date = date;
		   this.name = name;
		   this.comment = comment;
	   }
	   
	   public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Comment() {
		   
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

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }
}
