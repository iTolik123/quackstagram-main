package com.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;



public class InstagramProfileUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int PROFILE_IMAGE_SIZE = 80; // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = WIDTH / 3; // Static size for grid images
    private static final int NAV_ICON_SIZE = 20; // Corrected static size for bottom icons
    private JPanel contentPanel; // Panel to display the image grid or the clicked image
    private JPanel headerPanel;   // Panel for the header
    private JPanel navigationPanel; // Panel for the navigation
    private User currentUser; // User object to store the current user's information

    public InstagramProfileUI(User user) {
        this.currentUser = user;
        DbManager dbManager = new DbManager();
         // Initialize counts
        int imageCount = 0;
        int followersCount = 0;
        int followingCount = 0;
        
        imageCount = dbManager.getImageCount(currentUser.getUsername());

        followersCount = dbManager.getNumberOfFollowers(currentUser.getUsername());

        followingCount = dbManager.getNumberOfFollowing(currentUser.getUsername());

        String bio = dbManager.getBio(currentUser.getUsername());
    
        System.out.println("Bio for " + currentUser.getUsername() + ": " + bio);
        currentUser.setBio(bio);
        

        currentUser.setFollowersCount(followersCount);
        currentUser.setFollowingCount(followingCount);
        currentUser.setPostCount(imageCount);

        System.out.println(currentUser.getPostsCount());

        setTitle("DACS Profile");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        headerPanel = createHeaderPanel();       // Initialize header panel
        navigationPanel = createNavigationPanel(); // Initialize navigation panel

        initializeUI();
    }


    public InstagramProfileUI() {

        setTitle("DACS Profile");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        headerPanel = createHeaderPanel();       // Initialize header panel
        navigationPanel = createNavigationPanel(); // Initialize navigation panel
        initializeUI();
    }

    private void initializeUI() {
        getContentPane().removeAll(); // Clear existing components
        
        // Re-add the header and navigation panels
        add(headerPanel, BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.SOUTH);

        // Initialize the image grid
        initializeImageGrid();

        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        DbManager db = new DbManager();
        boolean isCurrentUser = false;
        String loggedInUsername = "";

        try (BufferedReader reader = Files.newBufferedReader(Paths.get("quackstagram_db-main/quackstagram_db/src/data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                loggedInUsername = line.split(":")[0].trim();
                isCurrentUser = loggedInUsername.equals(currentUser.getUsername());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    
       // Header Panel
        JPanel headerPanel = new JPanel();
        try (Stream<String> lines = Files.lines(Paths.get("quackstagram_db-main/quackstagram_db/src/data", "users.txt"))) {
            isCurrentUser = lines.anyMatch(line -> line.startsWith(currentUser.getUsername() + ":"));
        } catch (IOException e) {
            e.printStackTrace();  // Log or handle the exception as appropriate
        }

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.GRAY);
        
        // Top Part of the Header (Profile Image, Stats, Follow Button)
        JPanel topHeaderPanel = new JPanel(new BorderLayout(10, 0));
        topHeaderPanel.setBackground(new Color(249, 249, 249));

        // Profile image
        ImageIcon profileIcon = new ImageIcon(new ImageIcon(db.getProfileImage(currentUser.getUsername())).getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH));
        JLabel profileImage = new JLabel(profileIcon);
        profileImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topHeaderPanel.add(profileImage, BorderLayout.WEST);

        // Stats Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        statsPanel.setBackground(new Color(249, 249, 249));
        System.out.println("Number of posts for this user"+currentUser.getPostsCount());
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getPostsCount()) , "Posts"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowersCount()), "Followers"));
        statsPanel.add(createStatLabel(Integer.toString(currentUser.getFollowingCount()), "Following"));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0)); // Add some vertical padding

        
// Follow Button
// Follow or Edit Profile Button
// followButton.addActionListener(e -> handleFollowAction(currentUser.getUsername()));
JButton followButton;
    if (isCurrentUser) {
        followButton = new JButton("Edit Profile");
    } else {
        followButton = new JButton("Follow");

        // Check if the current user is already being followed by the logged-in user
        DbManager dbManager = new DbManager();
        boolean isFollowing = dbManager.isFollowing(loggedInUsername, currentUser.getUsername());
        if (isFollowing) {
            followButton.setText("Following");
        }
        followButton.addActionListener(e -> {
            
            followButton.setText("Following");
        });
    }
    
followButton.setAlignmentX(Component.CENTER_ALIGNMENT);
followButton.setFont(new Font("Arial", Font.BOLD, 12));
followButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, followButton.getMinimumSize().height)); // Make the button fill the horizontal space
followButton.setBackground(new Color(225, 228, 232)); // A soft, appealing color that complements the UI
followButton.setForeground(Color.BLACK);
followButton.setOpaque(true);
followButton.setBorderPainted(false);
followButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some vertical padding

        
        // Add Stats and Follow Button to a combined Panel
        JPanel statsFollowPanel = new JPanel();
        statsFollowPanel.setLayout(new BoxLayout(statsFollowPanel, BoxLayout.Y_AXIS));
        statsFollowPanel.add(statsPanel);
        statsFollowPanel.add(followButton);
        topHeaderPanel.add(statsFollowPanel, BorderLayout.CENTER);

        headerPanel.add(topHeaderPanel);

     // Profile Name and Bio Panel
JPanel profileNameAndBioPanel = new JPanel();
profileNameAndBioPanel.setLayout(new BorderLayout());
profileNameAndBioPanel.setBackground(new Color(249, 249, 249));

JLabel profileNameLabel = new JLabel(currentUser.getUsername());
profileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
profileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Padding on the sides

JTextArea profileBio = new JTextArea(currentUser.getBio());
System.out.println("This is the bio "+currentUser.getUsername());
profileBio.setEditable(false);
profileBio.setFont(new Font("Arial", Font.PLAIN, 12));
profileBio.setBackground(new Color(249, 249, 249));
profileBio.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding on the sides

profileNameAndBioPanel.add(profileNameLabel, BorderLayout.NORTH);
profileNameAndBioPanel.add(profileBio, BorderLayout.CENTER);

headerPanel.add(profileNameAndBioPanel);


        
        return headerPanel;

    }


    

    
    
    private JPanel createNavigationPanel() {
        // Navigation Bar
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(new Color(249, 249, 249));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        navigationPanel.add(createIconButton("quackstagram_db-main/quackstagram_db/src/img/icons/home.png", "home"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("quackstagram_db-main/quackstagram_db/src/img/icons/search.png","explore"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("quackstagram_db-main/quackstagram_db/src//img/icons/add.png","add"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("quackstagram_db-main/quackstagram_db/src/img/icons/heart.png","notification"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("quackstagram_db-main/quackstagram_db/src/mg/icons/profile.png", "profile"));

        return navigationPanel;

    }

    private void initializeImageGrid() {
        contentPanel.removeAll(); // Clear existing content
        contentPanel.setLayout(new GridLayout(0, 3, 5, 5)); // Grid layout for image grid

        DbManager dbManager = new DbManager();
        dbManager.getUserImages(currentUser.getUsername()).forEach(imagePath -> {
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(imagePath).getImage().getScaledInstance(GRID_IMAGE_SIZE, GRID_IMAGE_SIZE, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayImage(imageIcon); // Call method to display the clicked image
            }
            });
            contentPanel.add(imageLabel);
        });

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center

        revalidate();
        repaint();
    }



    private void displayImage(ImageIcon imageIcon) {
        contentPanel.removeAll(); // Remove existing content
        contentPanel.setLayout(new BorderLayout()); // Change layout for image display

        JLabel fullSizeImageLabel = new JLabel(imageIcon);
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(fullSizeImageLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            getContentPane().removeAll(); // Remove all components from the frame
            initializeUI(); // Re-initialize the UI
        });
        contentPanel.add(backButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }



    private JLabel createStatLabel(String number, String text) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + number + "<br/>" + text + "</div></html>", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.BLACK);
        return label;
    }

    private JButton createIconButton(String iconPath, String buttonType) {
        ImageIcon iconOriginal = new ImageIcon(iconPath);
        Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(iconScaled));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
    
        // Define actions based on button type
        if ("home".equals(buttonType)) {
            button.addActionListener(e -> openHomeUI());
        } else if ("profile".equals(buttonType)) {
            //
        } else if ("notification".equals(buttonType)) {
            button.addActionListener(e -> notificationsUI());
        } else if ("explore".equals(buttonType)) {
            button.addActionListener(e -> exploreUI());
        } else if ("add".equals(buttonType)) {
            button.addActionListener(e -> ImageUploadUI());
        }
        return button;
    
        
    }
 
    private void ImageUploadUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        ImageUploadUI upload = new ImageUploadUI();
        upload.setVisible(true);
    }

    private void openProfileUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        InstagramProfileUI profileUI = new InstagramProfileUI();
        profileUI.setVisible(true);
    }
 
     private void notificationsUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        NotificationsUI notificationsUI = new NotificationsUI();
        notificationsUI.setVisible(true);
    }
 
    private void openHomeUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        QuakstagramHomeUI homeUI = new QuakstagramHomeUI();
        homeUI.setVisible(true);
    }
 
    private void exploreUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        ExploreUI explore = new ExploreUI();
        explore.setVisible(true);
    }   

    
}
