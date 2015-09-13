/**
 * 
 */
package Client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.*;

import org.apache.commons.lang3.StringUtils;

import Server.GameInfo;
import Server.Server;


/**
 * @author a0134505
 *
 */
public class ClientImpl extends UnicastRemoteObject implements ActionListener, Serializable, Client{
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
	public JLabel password = new JLabel("PASSWORD",JLabel.LEFT);
	public JTextField userNameField = new JTextField();
	public JPasswordField passwordField = new JPasswordField();
	public JButton logIn = new JButton();
	public JButton signUp = new JButton();
    public int gridSize=0;
    public boolean playerAddCheck=false;
    public boolean hasGamestarted = false;
    private GameInfo gameInfo = null;
	

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
		impl.initLoginFrame();
		
		
		Registry registry = LocateRegistry.getRegistry("127.0.0.1", Constant.RMIPORT);
		Server serverObj = (Server) registry.lookup(Constant.RMIID);
		
		while(!impl.playerAddCheck){
			Thread.sleep(100);
		}
		
		while(impl.hasGamestarted){
			Thread.sleep(100);
		}
		
	    impl.displayMaze(impl.gameInfo);
	    
	    
		
	}catch(Exception ee){
			System.err.println("Error Initializing the game !! Game will exit now...");
			JOptionPane.showMessageDialog(null,
	                "Error Initializing the game !! Game will Exit now");
			
			
		}
		
		
		
		
	}

	
	private void displayMaze(GameInfo gameInfo2) {
		// TODO Auto-generated method stub
		
	}

	public void notifyGameStarted(GameInfo gameinfo){
		this.gameInfo = gameinfo;
		this.hasGamestarted= true;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent buttonClick) {
		// TODO Auto-generated method stub
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
						if(this.serverObj.addUser(userName, password)){
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
							panel.repaint();
						}else{
							JOptionPane.showMessageDialog(null,
					                "Please enter the correct password");
						}
					}catch(Exception e){

					}
				}
			}
		}
	}


}
