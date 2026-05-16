import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

// ============================================================
//  ESCAPE ROOM GAME - Java Swing GUI
//  Algorithms: Bubble Sort, Insertion Sort, Quick Sort,
//              Merge Sort, Binary Search, Linear Search
// ============================================================

public class EscapeRoomGame extends JFrame {

    static final Color BG_DARK    = new Color(10,  12,  20);
    static final Color BG_PANEL   = new Color(18,  22,  35);
    static final Color BG_CARD    = new Color(24,  30,  48);
    static final Color ACCENT     = new Color(0,   255, 180);
    static final Color ACCENT2    = new Color(255, 80,  120);
    static final Color GOLD       = new Color(255, 200, 50);
    static final Color TEXT_MAIN  = new Color(220, 230, 255);
    static final Color TEXT_DIM   = new Color(100, 120, 160);
    static final Color LOCKED     = new Color(180, 30,  50);
    static final Color UNLOCKED   = new Color(30,  180, 100);

    int currentRoom = 0;
    boolean[] roomCleared = {false, false, false, false};
    JPanel mainPanel;
    CardLayout cardLayout;
    JTextArea logArea;

    static int[] serverDisks;
    static int[] vaultCodes;
    static int[] prisonNumbers;

    public static void main(String[] args) {
        serverDisks   = new int[]{64, 21, 10, 88, 32, 7, 45, 99, 3, 56};
        vaultCodes    = new int[]{15, 42, 8, 77, 23, 5, 61, 30, 90, 12};
        prisonNumbers = new int[]{37, 14, 82, 9, 55, 28, 71, 46, 63, 19};

        SwingUtilities.invokeLater(() -> {
            EscapeRoomGame game = new EscapeRoomGame();
            game.setVisible(true);
        });
    }

    public EscapeRoomGame() {
        setTitle("ESCAPE ROOM — Algorithm Challenge");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(BG_DARK);

        mainPanel.add(buildIntroScreen(),   "INTRO");
        mainPanel.add(buildMapScreen(),     "MAP");
        mainPanel.add(buildRoom1(),         "ROOM1");
        mainPanel.add(buildRoom2(),         "ROOM2");
        mainPanel.add(buildRoom3(),         "ROOM3");
        mainPanel.add(buildRoom4(),         "ROOM4");
        mainPanel.add(buildWinScreen(),     "WIN");

        add(mainPanel);
        cardLayout.show(mainPanel, "INTRO");
    }

    JPanel buildIntroScreen() {
        JPanel p = darkPanel();
        p.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridwidth = GridBagConstraints.REMAINDER;
        g.insets    = new Insets(8, 0, 8, 0);
        g.anchor    = GridBagConstraints.CENTER;

        JLabel title = neonLabel("⬡  ESCAPE ROOM", 42, ACCENT);
        JLabel sub   = neonLabel("ALGORITHM CHALLENGE", 16, ACCENT2);
        JLabel desc  = styledLabel("Escape each themed room by answering algorithm questions correctly.", 14, TEXT_DIM);

        JPanel rulesCard = roundCard();
        rulesCard.setLayout(new BoxLayout(rulesCard, BoxLayout.Y_AXIS));
        rulesCard.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        String[] rules = {
                "🔒  Room 1 — Ancient Library           →  Bubble Sort",
                "🔒  Room 2 — Cyber Laboratory          →  Insertion Sort + Linear Search",
                "🔒  Room 3 — Secret Vault              →  Binary Search",
                "🔒  Room 4 — Control Chamber           →  Quick Sort + Merge Sort"
        };

        for (String r : rules) {
            JLabel l = styledLabel(r, 13, TEXT_MAIN);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            rulesCard.add(l);
            rulesCard.add(Box.createVerticalStrut(4));
        }

        GlowButton btn = new GlowButton("▶  START GAME", ACCENT);
        btn.addActionListener(e -> cardLayout.show(mainPanel, "MAP"));

        p.add(title,     g);
        p.add(sub,       g);
        p.add(Box.createVerticalStrut(10), g);
        p.add(desc,      g);
        p.add(Box.createVerticalStrut(14), g);
        p.add(rulesCard, g);
        p.add(Box.createVerticalStrut(20), g);
        p.add(btn,       g);

        return p;
    }

    JPanel buildMapScreen() {
        JPanel outer = darkPanel();
        outer.setLayout(new BorderLayout(0,0));

        JLabel title = neonLabel("SELECT ROOM", 24, ACCENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(24,0,16,0));
        outer.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        String[] rooms = {
                "Room 1\nAncient Library\nBubble Sort",
                "Room 2\nCyber Laboratory\nInsertion Sort + Linear Search",
                "Room 3\nSecret Vault\nBinary Search",
                "Room 4\nControl Chamber\nQuick Sort + Merge Sort"
        };

        String[] cards = {"ROOM1","ROOM2","ROOM3","ROOM4"};
        String[] icons = {"📚","🧪","🏦","⚔️"};

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            JPanel card = roomMapCard(icons[i], rooms[i], i);

            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (idx == 0 || roomCleared[idx-1]) {
                        cardLayout.show(mainPanel, cards[idx]);
                    } else {
                        JOptionPane.showMessageDialog(EscapeRoomGame.this,
                                "🔒 Clear Room " + idx + " first!",
                                "Locked", JOptionPane.WARNING_MESSAGE);
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            });

            grid.add(card);
        }

        outer.add(grid, BorderLayout.CENTER);

        JLabel hint = styledLabel("Complete rooms in order to unlock the next one", 12, TEXT_DIM);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        hint.setBorder(BorderFactory.createEmptyBorder(12,0,20,0));
        outer.add(hint, BorderLayout.SOUTH);

        return outer;
    }

    JPanel roomMapCard(String icon, String info, int idx) {
        String[] parts = info.split("\n");

        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g2) {
                super.paintComponent(g2);
                Graphics2D g = (Graphics2D) g2;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color border = roomCleared[idx] ? UNLOCKED :
                        (idx == 0 || (idx > 0 && roomCleared[idx - 1])) ? ACCENT : LOCKED;

                g.setColor(BG_CARD);
                g.fillRoundRect(0,0,getWidth()-1,getHeight()-1,18,18);
                g.setColor(border);
                g.setStroke(new BasicStroke(2));
                g.drawRoundRect(1,1,getWidth()-3,getHeight()-3,17,17);
            }
        };

        card.setOpaque(false);
        card.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridwidth = GridBagConstraints.REMAINDER;
        g.insets = new Insets(4,8,4,8);

        card.add(styledLabel(icon, 32, TEXT_MAIN), g);
        card.add(styledLabel(parts[0], 15, GOLD), g);
        card.add(styledLabel(parts.length > 1 ? parts[1] : "", 12, TEXT_MAIN), g);
        card.add(styledLabel(parts.length > 2 ? parts[2] : "", 11, ACCENT2), g);

        Color statusColor = roomCleared[idx] ? UNLOCKED : LOCKED;

        card.add(styledLabel(
                roomCleared[idx] ? "✔ CLEARED" :
                        (idx == 0 || (idx > 0 && roomCleared[idx - 1])) ? "▶ ENTER" : "🔒 LOCKED",
                12,
                statusColor
        ), g);

        return card;
    }

    boolean askAlgorithmQuestion(String title, String question, String correctAnswer, String explanation) {
        String answer = JOptionPane.showInputDialog(
                this,
                title + "\n\n" + question + "\n\nType A, B, C, or D:"
        );

        if (answer == null) {
            return false;
        }

        if (answer.trim().equalsIgnoreCase(correctAnswer)) {
            JOptionPane.showMessageDialog(
                    this,
                    "✅ Correct! You may run the algorithm.",
                    "Correct Answer",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return true;
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "❌ Incorrect!\n\n" + explanation,
                    "Wrong Answer",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
    }

    int[] room1Data = {64, 21, 10, 88, 32, 7, 45, 99, 3, 56};
    JPanel arrPanel1;
    JTextArea log1;

    JPanel buildRoom1() {
        JPanel p = darkPanel();
        p.setLayout(new BorderLayout(0,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));

        p.add(roomHeader(
                "📚  Room 1 — Ancient Library",
                "Bubble Sort",
                "Arrange the cursed scroll numbers using Bubble Sort to unlock the hidden door."
        ), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0,10));
        center.setOpaque(false);

        arrPanel1 = arrayVisualPanel(room1Data, -1, -1);
        center.add(arrPanel1, BorderLayout.CENTER);

        log1 = logArea();
        log1.setText("Scroll numbers loaded: " + Arrays.toString(room1Data) + "\nAnswer the question, then run Bubble Sort.");
        JScrollPane sp = styledScroll(log1);
        sp.setPreferredSize(new Dimension(0, 180));
        center.add(sp, BorderLayout.SOUTH);

        p.add(center, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        btns.setOpaque(false);

        GlowButton run = new GlowButton("▶ Run Bubble Sort", ACCENT);
        GlowButton back = new GlowButton("← Map", TEXT_DIM);

        btns.add(back);
        btns.add(run);
        p.add(btns, BorderLayout.SOUTH);

        run.addActionListener(e -> {
            boolean correct = askAlgorithmQuestion(
                    "Bubble Sort Question",
                    "What is the average time complexity of Bubble Sort?\n" +
                            "A) O(log n)\n" +
                            "B) O(n)\n" +
                            "C) O(n²)\n" +
                            "D) O(n log n)",
                    "C",
                    "Bubble Sort has average time complexity O(n²)."
            );

            if (correct) {
                animateBubbleSort();
            }
        });

        back.addActionListener(e -> cardLayout.show(mainPanel, "MAP"));

        return p;
    }

    void animateBubbleSort() {
        int[] arr = room1Data.clone();

        log1.setText("⚙ Starting Bubble Sort...\nInitial: " + Arrays.toString(arr) + "\n\n");

        int n = arr.length;

        javax.swing.SwingWorker<Void,int[]> worker = new javax.swing.SwingWorker<>() {
            protected Void doInBackground() throws Exception {
                for (int i=0;i<n-1;i++) {
                    for (int j=0;j<n-1-i;j++) {
                        Thread.sleep(350);

                        if (arr[j] > arr[j+1]) {
                            int tmp = arr[j];
                            arr[j] = arr[j+1];
                            arr[j+1] = tmp;

                            final int fj = j;

                            SwingUtilities.invokeLater(() -> {
                                log1.append("  Swap index " + fj + " ↔ " + (fj + 1) + " → " + Arrays.toString(arr) + "\n");
                                refreshArrayPanel1(arr, fj, fj + 1);
                            });
                        }
                    }
                }

                return null;
            }

            protected void done() {
                room1Data = arr;
                refreshArrayPanel1(arr,-1,-1);
                log1.append("\n✅ Sorted: " + Arrays.toString(arr));
                log1.append("\n\n🔓 ROOM 1 CLEARED! Door unlocked.");
                roomCleared[0] = true;
                showClearDialog("Room 1 Cleared!", "Bubble Sort complete.\nProceed to Room 2.");
            }
        };

        worker.execute();
    }

    void refreshArrayPanel1(int[] arr, int hi, int hj) {
        arrPanel1.removeAll();

        for (int i=0;i<arr.length;i++) {
            Color c = (i==hi||i==hj) ? ACCENT2 : ACCENT;
            arrPanel1.add(barBlock(arr[i], c, 99));
        }

        arrPanel1.revalidate();
        arrPanel1.repaint();
    }

    int[] room2Data;
    JPanel arrPanel2;
    JTextArea log2;
    boolean room2Sorted = false;

    JPanel buildRoom2() {
        room2Data = serverDisks.clone();

        JPanel p = darkPanel();
        p.setLayout(new BorderLayout(0,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));

        p.add(roomHeader(
                "🧪  Room 2 — Cyber Laboratory",
                "Insertion Sort + Linear Search",
                "Reorder the corrupted lab disks, then locate disk 32 using Linear Search."
        ), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0,10));
        center.setOpaque(false);

        arrPanel2 = arrayVisualPanel(room2Data,-1,-1);
        center.add(arrPanel2, BorderLayout.CENTER);

        log2 = logArea();
        log2.setText("Cyber lab disks: " + Arrays.toString(room2Data));
        center.add(styledScroll(log2), BorderLayout.SOUTH);

        p.add(center, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER,14,0));
        btns.setOpaque(false);

        GlowButton sort  = new GlowButton("▶ Insertion Sort", ACCENT);
        GlowButton srch  = new GlowButton("🔍 Linear Search (32)", GOLD);
        GlowButton back  = new GlowButton("← Map", TEXT_DIM);

        btns.add(back);
        btns.add(sort);
        btns.add(srch);

        p.add(btns, BorderLayout.SOUTH);

        sort.addActionListener(e -> {
            boolean correct = askAlgorithmQuestion(
                    "Insertion Sort Question",
                    "What is the average time complexity of Insertion Sort?\n" +
                            "A) O(log n)\n" +
                            "B) O(n²)\n" +
                            "C) O(n log n)\n" +
                            "D) O(1)",
                    "B",
                    "Insertion Sort has average time complexity O(n²)."
            );

            if (correct) {
                animateInsertionSort();
            }
        });

        srch.addActionListener(e -> {
            if (!room2Sorted) {
                JOptionPane.showMessageDialog(this,"Sort first!","Not sorted",JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean correct = askAlgorithmQuestion(
                    "Linear Search Question",
                    "What is the time complexity of Linear Search in the worst case?\n" +
                            "A) O(1)\n" +
                            "B) O(log n)\n" +
                            "C) O(n)\n" +
                            "D) O(n²)",
                    "C",
                    "Linear Search may check every element, so its worst-case time complexity is O(n)."
            );

            if (correct) {
                runLinearSearch(32);
            }
        });

        back.addActionListener(e -> cardLayout.show(mainPanel, "MAP"));

        return p;
    }

    void animateInsertionSort() {
        int[] arr = room2Data.clone();

        log2.setText("⚙ Starting Insertion Sort...\nInitial: " + Arrays.toString(arr) + "\n\n");

        javax.swing.SwingWorker<Void,Void> w = new javax.swing.SwingWorker<>() {
            protected Void doInBackground() throws Exception {
                for (int i=1;i<arr.length;i++) {
                    int key = arr[i];
                    int j = i - 1;

                    while(j >= 0 && arr[j] > key) {
                        arr[j+1] = arr[j];
                        j--;
                    }

                    arr[j+1] = key;

                    Thread.sleep(400);

                    final int fi = i;
                    final int fj = j + 1;

                    SwingUtilities.invokeLater(() -> {
                        log2.append("  Insert " + key + " at pos " + fj + " → " + Arrays.toString(arr) + "\n");
                        refreshArrPanel2(arr, fi, fj);
                    });
                }

                return null;
            }

            protected void done() {
                room2Data = arr;
                room2Sorted = true;
                refreshArrPanel2(arr,-1,-1);
                log2.append("\n✅ Sorted: " + Arrays.toString(arr) + "\nNow use Linear Search to find disk 32.");
            }
        };

        w.execute();
    }

    void runLinearSearch(int target) {
        log2.append("\n🔍 Linear Search for " + target + " …\n");

        javax.swing.SwingWorker<Void,Void> w = new javax.swing.SwingWorker<>() {
            protected Void doInBackground() throws Exception {
                for (int i=0;i<room2Data.length;i++) {
                    Thread.sleep(300);

                    final int fi = i;

                    SwingUtilities.invokeLater(() -> {
                        refreshArrPanel2(room2Data, fi, -1);
                        log2.append("  Checking index " + fi + " → " + room2Data[fi] + "\n");
                    });

                    if (room2Data[i] == target) {
                        Thread.sleep(200);

                        SwingUtilities.invokeLater(() -> {
                            refreshArrPanel2(room2Data, fi, fi);
                            log2.append("\n✅ Found " + target + " at index " + fi + "! ROOM 2 CLEARED.\n");
                            roomCleared[1] = true;
                            showClearDialog("Room 2 Cleared!","Disk 32 located via Linear Search.\nProceed to Room 3.");
                        });

                        return null;
                    }
                }

                return null;
            }

            protected void done(){}
        };

        w.execute();
    }

    void refreshArrPanel2(int[] arr,int hi,int hj) {
        arrPanel2.removeAll();

        for(int i=0;i<arr.length;i++) {
            Color c = (i==hj) ? UNLOCKED : (i==hi) ? GOLD : ACCENT;
            arrPanel2.add(barBlock(arr[i], c, 99));
        }

        arrPanel2.revalidate();
        arrPanel2.repaint();
    }

    int[] room3Data;
    JPanel arrPanel3;
    JTextArea log3;

    JPanel buildRoom3() {
        room3Data = vaultCodes.clone();
        Arrays.sort(room3Data);

        JPanel p = darkPanel();
        p.setLayout(new BorderLayout(0,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));

        p.add(roomHeader(
                "🏦  Room 3 — Secret Vault",
                "Binary Search",
                "The vault codes are already sorted. Use Binary Search to find access code 61."
        ), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0,10));
        center.setOpaque(false);

        arrPanel3 = arrayVisualPanel(room3Data,-1,-1);
        center.add(arrPanel3, BorderLayout.CENTER);

        log3 = logArea();
        log3.setText("Sorted vault codes: " + Arrays.toString(room3Data) + "\nTarget: 61");
        center.add(styledScroll(log3), BorderLayout.SOUTH);

        p.add(center, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER,14,0));
        btns.setOpaque(false);

        GlowButton srch = new GlowButton("🔍 Binary Search (61)", ACCENT);
        GlowButton back = new GlowButton("← Map", TEXT_DIM);

        btns.add(back);
        btns.add(srch);

        p.add(btns, BorderLayout.SOUTH);

        srch.addActionListener(e -> {
            boolean correct = askAlgorithmQuestion(
                    "Binary Search Question",
                    "What is the time complexity of Binary Search?\n" +
                            "A) O(n)\n" +
                            "B) O(n²)\n" +
                            "C) O(log n)\n" +
                            "D) O(n log n)",
                    "C",
                    "Binary Search cuts the search range in half each step, so its time complexity is O(log n)."
            );

            if (correct) {
                animateBinarySearch(61);
            }
        });

        back.addActionListener(e -> cardLayout.show(mainPanel, "MAP"));

        return p;
    }

    void animateBinarySearch(int target) {
        log3.setText("🔍 Binary Search for " + target + "…\nSorted: " + Arrays.toString(room3Data) + "\n\n");

        javax.swing.SwingWorker<Void,Void> w = new javax.swing.SwingWorker<>() {
            protected Void doInBackground() throws Exception {
                int lo = 0;
                int hi = room3Data.length - 1;

                while(lo <= hi) {
                    int mid = (lo + hi) / 2;

                    final int flo = lo;
                    final int fhi = hi;
                    final int fmid = mid;

                    SwingUtilities.invokeLater(() -> {
                        refreshArrPanel3(room3Data, flo, fhi, fmid);
                        log3.append("  lo=" + flo + " hi=" + fhi + " mid=" + fmid + " → value=" + room3Data[fmid] + "\n");
                    });

                    Thread.sleep(700);

                    if(room3Data[mid] == target) {
                        final int fm = mid;

                        SwingUtilities.invokeLater(() -> {
                            refreshArrPanel3Found(room3Data, fm);
                            log3.append("\n✅ Found " + target + " at index " + fm + "! ROOM 3 CLEARED.\n");
                            roomCleared[2] = true;
                            showClearDialog("Room 3 Cleared!","Access code found via Binary Search.\nProceed to Room 4.");
                        });

                        return null;
                    } else if(room3Data[mid] < target) {
                        lo = mid + 1;
                    } else {
                        hi = mid - 1;
                    }
                }

                return null;
            }

            protected void done(){}
        };

        w.execute();
    }

    void refreshArrPanel3(int[] arr,int lo,int hi,int mid) {
        arrPanel3.removeAll();

        for(int i=0;i<arr.length;i++) {
            Color c = (i==mid) ? GOLD : (i>=lo && i<=hi) ? ACCENT : TEXT_DIM;
            arrPanel3.add(barBlock(arr[i], c, 99));
        }

        arrPanel3.revalidate();
        arrPanel3.repaint();
    }

    void refreshArrPanel3Found(int[] arr,int found) {
        arrPanel3.removeAll();

        for(int i=0;i<arr.length;i++) {
            arrPanel3.add(barBlock(arr[i], i==found ? UNLOCKED : TEXT_DIM, 99));
        }

        arrPanel3.revalidate();
        arrPanel3.repaint();
    }

    int[] room4Quick;
    int[] room4Merge;
    JPanel arrPanelQ, arrPanelM;
    JTextArea log4;
    boolean quickDone = false;
    boolean mergeDone = false;

    JPanel buildRoom4() {
        room4Quick = prisonNumbers.clone();
        room4Merge = prisonNumbers.clone();

        JPanel p = darkPanel();
        p.setLayout(new BorderLayout(0,10));
        p.setBorder(BorderFactory.createEmptyBorder(16,20,16,20));

        p.add(roomHeader(
                "⚔️  Room 4 — Control Chamber",
                "Quick Sort + Merge Sort",
                "Solve both final sorting challenges to escape the control chamber."
        ), BorderLayout.NORTH);

        JPanel twoCol = new JPanel(new GridLayout(1,2,12,0));
        twoCol.setOpaque(false);

        JPanel qp = roundCard();
        qp.setLayout(new BorderLayout(0,6));
        qp.add(styledLabel("⚡ Quick Sort",14,ACCENT), BorderLayout.NORTH);
        arrPanelQ = arrayVisualPanel(room4Quick,-1,-1);
        qp.add(arrPanelQ, BorderLayout.CENTER);
        twoCol.add(qp);

        JPanel mp = roundCard();
        mp.setLayout(new BorderLayout(0,6));
        mp.add(styledLabel("🔀 Merge Sort",14,ACCENT2), BorderLayout.NORTH);
        arrPanelM = arrayVisualPanel(room4Merge,-1,-1);
        mp.add(arrPanelM, BorderLayout.CENTER);
        twoCol.add(mp);

        JPanel center = new JPanel(new BorderLayout(0,8));
        center.setOpaque(false);
        center.add(twoCol, BorderLayout.CENTER);

        log4 = logArea();
        log4.setText("Control chamber data: " + Arrays.toString(prisonNumbers));
        center.add(styledScroll(log4), BorderLayout.SOUTH);

        p.add(center, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER,14,0));
        btns.setOpaque(false);

        GlowButton bq   = new GlowButton("⚡ Quick Sort", ACCENT);
        GlowButton bm   = new GlowButton("🔀 Merge Sort", ACCENT2);
        GlowButton back = new GlowButton("← Map", TEXT_DIM);

        btns.add(back);
        btns.add(bq);
        btns.add(bm);

        p.add(btns, BorderLayout.SOUTH);

        bq.addActionListener(e -> {
            boolean correct = askAlgorithmQuestion(
                    "Quick Sort Question",
                    "What is the average time complexity of Quick Sort?\n" +
                            "A) O(n²)\n" +
                            "B) O(n)\n" +
                            "C) O(log n)\n" +
                            "D) O(n log n)",
                    "D",
                    "Quick Sort has average time complexity O(n log n)."
            );

            if (correct) {
                animateQuickSort();
            }
        });

        bm.addActionListener(e -> {
            boolean correct = askAlgorithmQuestion(
                    "Merge Sort Question",
                    "What is the time complexity of Merge Sort?\n" +
                            "A) O(n log n)\n" +
                            "B) O(n²)\n" +
                            "C) O(log n)\n" +
                            "D) O(1)",
                    "A",
                    "Merge Sort has time complexity O(n log n)."
            );

            if (correct) {
                animateMergeSort();
            }
        });

        back.addActionListener(e -> cardLayout.show(mainPanel, "MAP"));

        return p;
    }

    void animateQuickSort() {
        int[] arr = room4Quick.clone();

        log4.setText("⚡ Quick Sort starting…\nInitial: " + Arrays.toString(arr) + "\n\n");

        new javax.swing.SwingWorker<Void,Void>() {
            protected Void doInBackground() throws Exception {
                quickSortHelper(arr,0,arr.length-1);
                return null;
            }

            void quickSortHelper(int[] a,int lo,int hi) throws Exception {
                if(lo < hi) {
                    int p = partition(a,lo,hi);
                    quickSortHelper(a,lo,p-1);
                    quickSortHelper(a,p+1,hi);
                }
            }

            int partition(int[] a,int lo,int hi) throws Exception {
                int pivot = a[hi];
                int i = lo - 1;

                for(int j=lo;j<hi;j++) {
                    if(a[j] <= pivot) {
                        i++;

                        int t = a[i];
                        a[i] = a[j];
                        a[j] = t;

                        Thread.sleep(250);

                        final int[] snap = a.clone();

                        SwingUtilities.invokeLater(() -> refreshQPanel(snap,hi,-1));
                        SwingUtilities.invokeLater(() -> log4.append("  pivot=" + pivot + " swap → " + Arrays.toString(snap) + "\n"));
                    }
                }

                int t = a[i+1];
                a[i+1] = a[hi];
                a[hi] = t;

                return i+1;
            }

            protected void done() {
                room4Quick = arr;
                refreshQPanel(arr,-1,-1);
                log4.append("\n✅ Quick Sort done: " + Arrays.toString(arr) + "\n");
                quickDone = true;
                checkRoom4();
            }
        }.execute();
    }

    void animateMergeSort() {
        int[] arr = room4Merge.clone();

        log4.append("\n🔀 Merge Sort starting…\nInitial: " + Arrays.toString(arr) + "\n\n");

        new javax.swing.SwingWorker<Void,Void>() {
            protected Void doInBackground() throws Exception {
                mergeSortHelper(arr,0,arr.length-1);
                return null;
            }

            void mergeSortHelper(int[] a,int l,int r) throws Exception {
                if(l < r) {
                    int m = (l + r) / 2;
                    mergeSortHelper(a,l,m);
                    mergeSortHelper(a,m+1,r);
                    merge(a,l,m,r);
                }
            }

            void merge(int[] a,int l,int m,int r) throws Exception {
                int n1 = m - l + 1;
                int n2 = r - m;

                int[] L = new int[n1];
                int[] R = new int[n2];

                for(int i=0;i<n1;i++) {
                    L[i] = a[l+i];
                }

                for(int j=0;j<n2;j++) {
                    R[j] = a[m+1+j];
                }

                int i = 0;
                int j = 0;
                int k = l;

                while(i < n1 && j < n2) {
                    a[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
                }

                while(i < n1) {
                    a[k++] = L[i++];
                }

                while(j < n2) {
                    a[k++] = R[j++];
                }

                Thread.sleep(300);

                final int[] snap = a.clone();

                SwingUtilities.invokeLater(() -> {
                    refreshMPanel(snap,-1,-1);
                    log4.append("  merge [" + l + ".." + r + "] → " + Arrays.toString(snap) + "\n");
                });
            }

            protected void done() {
                room4Merge = arr;
                refreshMPanel(arr,-1,-1);
                log4.append("\n✅ Merge Sort done: " + Arrays.toString(arr) + "\n");
                mergeDone = true;
                checkRoom4();
            }
        }.execute();
    }

    void checkRoom4() {
        if(quickDone && mergeDone) {
            log4.append("\n🎉 BOTH SORTS COMPLETE — ROOM 4 CLEARED!\n");
            roomCleared[3] = true;
            showClearDialog("Room 4 Cleared!","Quick Sort & Merge Sort complete!\nYOU ESCAPED!");

            Timer t = new Timer(1200, e -> cardLayout.show(mainPanel,"WIN"));
            t.setRepeats(false);
            t.start();
        }
    }

    void refreshQPanel(int[] arr,int hi,int hj) {
        arrPanelQ.removeAll();

        for(int i=0;i<arr.length;i++) {
            arrPanelQ.add(barBlock(arr[i], (i==hi) ? GOLD : (i==hj) ? ACCENT2 : ACCENT, 88));
        }

        arrPanelQ.revalidate();
        arrPanelQ.repaint();
    }

    void refreshMPanel(int[] arr,int hi,int hj) {
        arrPanelM.removeAll();

        for(int i=0;i<arr.length;i++) {
            arrPanelM.add(barBlock(arr[i], (i==hi) ? GOLD : (i==hj) ? ACCENT : ACCENT2, 88));
        }

        arrPanelM.revalidate();
        arrPanelM.repaint();
    }

    JPanel buildWinScreen() {
        JPanel p = darkPanel();
        p.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridwidth = GridBagConstraints.REMAINDER;
        g.insets = new Insets(10,0,10,0);

        p.add(neonLabel("🎉  YOU ESCAPED!", 48, GOLD), g);
        p.add(neonLabel("All 4 rooms cleared!", 20, ACCENT), g);
        p.add(Box.createVerticalStrut(20), g);

        JPanel summary = roundCard();
        summary.setLayout(new BoxLayout(summary,BoxLayout.Y_AXIS));
        summary.setBorder(BorderFactory.createEmptyBorder(16,28,16,28));

        String[] algs = {
                "✅  Room 1 — Ancient Library       Bubble Sort       O(n²)",
                "✅  Room 2 — Cyber Laboratory      Insertion Sort     O(n²)  +  Linear Search O(n)",
                "✅  Room 3 — Secret Vault          Binary Search      O(log n)",
                "✅  Room 4 — Control Chamber       Quick Sort O(n log n)  +  Merge Sort O(n log n)"
        };

        for(String s : algs) {
            JLabel l = styledLabel(s,13,TEXT_MAIN);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            summary.add(l);
            summary.add(Box.createVerticalStrut(6));
        }

        p.add(summary,g);
        p.add(Box.createVerticalStrut(20),g);

        GlowButton replay = new GlowButton("↺  Play Again", ACCENT);

        replay.addActionListener(e -> {
            roomCleared = new boolean[]{false,false,false,false};
            room1Data = new int[]{64,21,10,88,32,7,45,99,3,56};
            room2Sorted = false;
            quickDone = false;
            mergeDone = false;
            cardLayout.show(mainPanel,"INTRO");
        });

        p.add(replay,g);

        return p;
    }

    JPanel darkPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG_DARK);
        return p;
    }

    JPanel roundCard() {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g2) {
                super.paintComponent(g2);

                Graphics2D g = (Graphics2D) g2;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                g.setColor(BG_CARD);
                g.fillRoundRect(0,0,getWidth(),getHeight(),16,16);

                g.setColor(new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),40));
                g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(1,1,getWidth()-2,getHeight()-2,15,15);
            }
        };

        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));

        return card;
    }

    JLabel neonLabel(String text, int size, Color c) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Monospaced", Font.BOLD, size));
        l.setForeground(c);
        return l;
    }

    JLabel styledLabel(String text, int size, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Monospaced", Font.PLAIN, size));
        l.setForeground(c);
        return l;
    }

    JTextArea logArea() {
        JTextArea a = new JTextArea(6, 40);
        a.setEditable(false);
        a.setBackground(new Color(10,14,25));
        a.setForeground(new Color(0,220,120));
        a.setFont(new Font("Monospaced", Font.PLAIN, 11));
        a.setBorder(BorderFactory.createEmptyBorder(6,8,6,8));
        a.setCaretColor(ACCENT);
        return a;
    }

    JScrollPane styledScroll(JTextArea a) {
        JScrollPane sp = new JScrollPane(a);
        sp.setBorder(BorderFactory.createLineBorder(new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),60)));
        sp.setPreferredSize(new Dimension(0,160));
        return sp;
    }

    JPanel arrayVisualPanel(int[] arr, int hi, int hj) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 10));
        p.setOpaque(false);

        for (int i=0;i<arr.length;i++) {
            Color c = (i==hi || i==hj) ? ACCENT2 : ACCENT;
            p.add(barBlock(arr[i], c, arr[i]));
        }

        return p;
    }

    JPanel barBlock(int value, Color c, int maxVal) {
        int h = Math.max(24, (int)(value * 80.0 / Math.max(maxVal,1)));

        JPanel bar = new JPanel() {
            protected void paintComponent(Graphics g2) {
                Graphics2D g = (Graphics2D) g2;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0,0,c.brighter(),0,getHeight(),c.darker());
                g.setPaint(gp);
                g.fillRoundRect(0,0,getWidth()-2,getHeight()-2,6,6);
            }
        };

        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(38, Math.min(h+20,110)));
        bar.setToolTipText(String.valueOf(value));
        bar.setLayout(new BorderLayout());

        JLabel lbl = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced",Font.BOLD,10));
        lbl.setForeground(Color.WHITE);

        bar.add(lbl, BorderLayout.SOUTH);

        return bar;
    }

    JPanel roomHeader(String title, String algo, String desc) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel t = neonLabel(title, 20, GOLD);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel a = neonLabel("Algorithm: " + algo, 13, ACCENT2);
        a.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel d = styledLabel(desc, 12, TEXT_DIM);
        d.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(t);
        p.add(a);
        p.add(d);
        p.add(Box.createVerticalStrut(6));

        p.add(new JSeparator() {
            {
                setForeground(new Color(ACCENT.getRed(),ACCENT.getGreen(),ACCENT.getBlue(),60));
                setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
            }
        });

        return p;
    }

    void showClearDialog(String title, String msg) {
        JOptionPane.showMessageDialog(
                this,
                "✅  " + msg,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );

        cardLayout.show(mainPanel,"MAP");
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    static class GlowButton extends JButton {
        Color glowColor;
        boolean hovered = false;

        GlowButton(String text, Color c) {
            super(text);

            this.glowColor = c;

            setFont(new Font("Monospaced",Font.BOLD,13));
            setForeground(c);
            setBackground(new Color(c.getRed(),c.getGreen(),c.getBlue(),18));

            setBorder(new CompoundBorder(
                    new LineBorder(c,1,true),
                    BorderFactory.createEmptyBorder(8,20,8,20)
            ));

            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        protected void paintComponent(Graphics g2) {
            Graphics2D g = (Graphics2D) g2;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

            if(hovered) {
                g.setColor(new Color(glowColor.getRed(),glowColor.getGreen(),glowColor.getBlue(),40));
                g.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
            }

            super.paintComponent(g2);
        }
    }
}