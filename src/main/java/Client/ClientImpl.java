/**
 * 
 */
package Client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;











//import server.CoordinatesUtil;
import Server.Coordinate;
import Server.GameInfo;
import Server.Server;


/**
 * @author a0134505
 *
 */
public class ClientImpl extends UnicastRemoteObject implements ActionListener,KeyListener, Serializable, Client,Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7437795715343481173L;
	
	public Server serverObj;
	public String userId= null;
	public JFrame gameFrame = new JFrame();
	public JPanel panel = new JPanel();
	public JLabel welcomeText = new JLabel("Welcome to Distributed Maze Game!!",JLabel.CENTER);
	public JLabel userName = new JLabel("USERID",JLabel.LEFT);
	public JTextField keyboardEntry = new JTextField();
	public JLabel password = new JLabel("PASSWORD",JLabel.LEFT);
	public JTextField userNameField = new JTextField();
	public JPasswordField passwordField = new JPasswordField();
	public JLabel mazeField = new JLabel("MAZE",JLabel.LEFT);
	public JButton logIn = new JButton();
	public JButton signUp = new JButton();
    public int gridSize=5;
    public boolean playerAddCheck=false;
    public boolean hasGamestarted = false;
    public boolean hasGameEnded = false;
    private GameInfo gameInfo = null;
    private Map<String,Coordinate> playerPostionMap = new ConcurrentHashMap<String,Coordinate>();
	
	
	private Map<Coordinate,Integer> treasureMap = new HashMap<Coordinate,Integer>();
	
	private Map<String,Integer> playerScoreMap = new ConcurrentHashMap<String, Integer>();
	

	protected ClientImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void initLoginFrame(){
		Color color = Color.black;
		gameFrame.setBackground(Color.BLUE);
		gameFrame.setVisible(true);

		gameFrame.setSize(500, 500);
		gameFrame.setLocation(200, 200);
		panel.setLayout(null);
		panel.setName("Login panel");
		panel.setVisible(true);
		panel.setBounds(0, 0, (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		panel.setBackground(Color.LIGHT_GRAY);
		gameFrame.getContentPane().add(panel);

		welcomeText.setLocation(0, 5);
		welcomeText.setSize(500, 20);
		welcomeText.setVisible(true);
		panel.add(welcomeText);

		userName.setLocation(0, 40);
		userName.setSize(70, 10);
		userName.setVisible(true);
		panel.add(userName);		

		userNameField.setName("userName");
		userNameField.setEditable(true);
		userNameField.setVisible(true);
		userNameField.setBounds(150, 40, 160, 20);
		panel.add(userNameField);		

		password.setLocation(0, 70);  
		password.setSize(70, 10);
		password.setVisible(true);
		panel.add(password);		

		passwordField.setName("password");
		passwordField.setEditable(true);
		passwordField.setVisible(true);
		passwordField.setBounds(150, 70, 160, 20);
		panel.add(passwordField);		



		logIn.setName("login");
		logIn.setVisible(true);
		logIn.setBounds(85, 125, 120, 20);
		logIn.setText("ENTER GAME!!");
		logIn.setActionCommand("login");
		logIn.addActionListener(this);
		panel.add(logIn);		



		gameFrame.getContentPane().add(panel);	

	}

	/**
	 * @param args
	 * @throws RemoteException 
	 * @throws NotBoundException 
	 */
	public static void main(String[] args) throws RemoteException, NotBoundException {
		// TODO Auto-generated method stub
		try{
		
			
		ClientImpl impl = new ClientImpl();
		Registry registry = LocateRegistry.getRegistry("127.0.0.1", Constant.RMIPORT);
		impl.serverObj = (Server) registry.lookup(Constant.RMIID);
		impl.initLoginFrame();
	/*	while(!impl.playerAddCheck){
			Thread.sleep(100);
		}
		*/
		while(!impl.hasGamestarted){
			Thread.sleep(100);
		}
		//if(impl.hasGamestarted){
		
		if(true){
			System.out.println("Player added.. Game has started");
		   
			impl.playerPostionMap= impl.serverObj.getPlayerPostionMap();	
			impl.playerScoreMap = impl.serverObj.getPlayerScoreMap();
			impl.treasureMap = impl.serverObj.getTreasureMap();
			impl.displayupdatedMaze();
			impl.readButtons();
			}
		}catch(Exception ee){
			System.err.println("Error Initializing the game !! Game will exit now...");
			JOptionPane.showMessageDialog(null,
	                "Error Initializing the game !! Game will Exit now");
			
			
		}
		
		
		
		
	}
	
	private  void readButtons() {
		// TODO Auto-generated method stub
		
		
		System.out.println("Game has started .. Please click the below buttons to start playing the game...\n"+
		                         "You are currently at position (0,0)");
		//panel.addKeyListener(new keyb());
		 
		 
		
		
			
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Here");
		if(!checkGameEnded()){
		try{
            boolean moved = false;
			playerPostionMap= this.serverObj.getPlayerPostionMap();	
			System.out.println(playerPostionMap.size());
			Coordinate c = playerPostionMap.get(userId);
			if(c!=null){
			System.out.println(c.toString());
			}else {
				System.out.println("c is null");
			}
			if(e.getKeyCode()==KeyEvent.VK_UP){
				System.out.println("UP");
				c.setRow(c.getRow()-1);
				c.setColumn(c.getColumn());
				
				System.out.println("Move is "+moved);
				moved = this.serverObj.moveUser(userId, c);
				System.out.println("Move is "+moved);
				if(moved){
				displayupdatedMaze();
				}else{
					System.out.println("Cannot Move");
				}
			}else if(e.getKeyCode()==KeyEvent.VK_DOWN){
				System.out.println("DOWN");
				c.setRow(c.getRow()+1);
				c.setColumn(c.getColumn());
				//System.out.println(c.toString());
				moved = this.serverObj.moveUser(userId, c);
				if(moved){
				displayupdatedMaze();
				}else{
					System.out.println("Cannot Move");
				}
			}else if(e.getKeyCode()==KeyEvent.VK_RIGHT){    
				System.out.println("RIGHT");
				c.setRow(c.getRow());
				c.setColumn(c.getColumn()+1);
				//System.out.println(c.toString());
				moved = this.serverObj.moveUser(userId, c);
				if(moved){
				displayupdatedMaze();
				}else{
					System.out.println("Cannot Move");
				}
			}else if(e.getKeyCode()==KeyEvent.VK_LEFT){
				System.out.println("LEFT");
				c.setRow(c.getRow());
				c.setColumn(c.getColumn()-1);
				//System.out.println(c.toString());
				moved = this.serverObj.moveUser(userId, c);
				if(moved){
				displayupdatedMaze();
				}else{
					System.out.println("Cannot Move");
				}
			}
			}catch(Exception e1){
                 System.out.println(e1.getMessage());
		}
		}else{
			System.out.println("Game has ended");
			try{
			playerScoreMap = serverObj.getPlayerScoreMap();
			System.out.println("***********************************************");
			System.out.println("FINAL SCORES\n");
			Set<String> scores = new HashSet<String>();
			scores = playerScoreMap.keySet();
			Iterator scoreIterator = scores.iterator();
			System.out.println(scores.size());
			while(scoreIterator.hasNext()){
				String user = (String) scoreIterator.next();
				System.out.print(user+" - ");
				System.out.println(playerScoreMap.get(user));
			}
		
			}catch(Exception mm){
				
			}
			
			}
	}

	
	

	private boolean checkGameEnded() {
		// TODO Auto-generated method stub
		this.hasGameEnded= false;
		if(treasureMap.size()==0){
			this.hasGameEnded=true;
		}
		return this.hasGameEnded;
	}

	private void displayupdatedMaze() {
		// TODO Auto-generated method stub
		//playerPostionMap.put("abhinav", new Coordinate(2, 2));
		//playerPostionMap.put("sarja", new Coordinate(4, 0));
		//treasureMap.put(new Coordinate(3,2), 2);
		//treasureMap.put(new Coordinate(4,3), 3);
		//Set<String> playerSet = playerPostionMap.keySet();
		System.out.println("***********************************************\n");
		
			
			for (int i =0;i<this.gridSize;i++){
			for(int j=0;j<this.gridSize;j++){
				String present = entityCheck(i,j);
				//System.out.println(present);
				if(present!=null){
					System.out.print(padString(present,10));
				}else{
					System.out.print(padString("_____",10));
				}
				//System.out.print();
				
			}
			
			System.out.println();
			System.out.println();
		
		}
		System.out.println("************************************************\n");		          
		
	}

	private String entityCheck(int i, int j) {
		// TODO Auto-generated method stub
		String present = null;
		//System.out.println(i+","+j);
		try{
		
		Set<String> playerSet = playerPostionMap.keySet();
		Iterator itr = playerSet.iterator();
		while(itr.hasNext()){
			String user = itr.next().toString();
			Coordinate c = (Coordinate)playerPostionMap.get(user);
			if(i==c.getRow() && j==c.getColumn()){
				//System.out.println("Player exists");
				present = user;
				return user;
			}
			
		}
		//System.out.println(treasureMap.size());
		treasureMap = this.serverObj.getTreasureMap();
		Set<Coordinate> treasureSet = treasureMap.keySet();
		Iterator itr1 = treasureSet.iterator();
		while(itr1.hasNext()){
			Coordinate c = (Coordinate) itr1.next();
			if(i==c.getRow() && j==c.getColumn()){
				//System.out.println("treasure exists");
				present = String.valueOf(treasureMap.get(c));
				return present;
				
			}
		}
		}catch(Exception e){
			System.out.println("Exception "+e.getMessage());
			return null;
		}
		return present;
	}

	public void notifyPlayer(boolean hasGameStarted){
		
		System.out.println("Game has started");
		this.hasGamestarted = true;
	}

	@Override
	public void notifyGameEnd(boolean gameEnded) throws RemoteException {
		// TODO Auto-generated method stub
		
		this.hasGameEnded = true;
		
	}
	

	
	
	@Override
	public void actionPerformed(ActionEvent buttonClick) {
		// TODO Auto-generated method stub
		System.out.println("Inside Action");
		JLabel errors = new JLabel();
		errors.setBounds(20, 130, 480, 80);
		errors.setForeground(Color.RED);	


		if(buttonClick.getSource() instanceof JButton){
			JButton button = (JButton) buttonClick.getSource();			
			String userName = this.userNameField.getText();
			StringBuilder passwordBuilder = new StringBuilder();
			for(char c : this.passwordField.getPassword()){				
				passwordBuilder.append(c);
			}

			String password = passwordBuilder.toString();			
			if(userName ==null || password==null || userName.isEmpty() || password.isEmpty()){				
				errors.setText("<html> Invalid Login!!.<br> Login Again!!</html>");
				errors.setVisible(true);				
				panel.add(errors);
				panel.repaint();
			}else{
				// when the login button is clicked, this method is invoked
				
				if("login".equals(button.getActionCommand())){
					try {
						
						//System.out.println("Output :"+this.serverObj.addUser(userName, password,this));
						if(this.serverObj.addUser(userName, password,this)){
						//if(true){
						System.out.println("Login Successful");
						    
							this.userId = this.userNameField.getText();
							for(Component component : panel.getComponents()){
								panel.remove(component);
							}
							JLabel welcomeUser = new JLabel();
							welcomeUser.setText("Welcome " + this.userId +  "," );
							welcomeUser.setVisible(true);
							welcomeUser.setBounds(0, 0, 500, 15);
							panel.add(welcomeUser);
							keyboardEntry.setLocation(0, 40);
							keyboardEntry.setBounds(150, 40, 160, 20);
							keyboardEntry.setVisible(true);
							keyboardEntry.addKeyListener(this);
							panel.add(keyboardEntry);		
							panel.repaint();
							this.playerAddCheck= true;
						}else{
							JOptionPane.showMessageDialog(null,
					                "Login failed !!\n"
					                + "Reasons:"
					                + " 1.User Exists."
					                + " 2.Wrong Password.");
						}
					}catch(Exception e){

					}
				}
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	

	public static String padString(String str, int leng) {
        for (int i = str.length(); i <= leng; i++)
            str += " ";
        return str;
    }

}
