package view;

import util.UIStyle;

import javax.swing.*;
import java.awt.*;

public class ConfirmDialog extends JDialog {

    private boolean confirmed = false;

    private ConfirmDialog(Frame owner, String title, String message) {
        super(owner, title, true);
        setSize(360, 180);
        setLocationRelativeTo(owner);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(UIStyle.getBackground());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UIStyle.getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());

                int arc = 18;
                int pad = 8;
                int w = getWidth() - pad * 2;
                int h = getHeight() - pad * 2;

                g2.setColor(UIStyle.getCardBackground());
                g2.fillRoundRect(pad, pad, w, h, arc, arc);

                g2.setColor(UIStyle.getCardBorder());
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRoundRect(pad, pad, w, h, arc, arc);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        add(card, gbc);

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        msgLabel.setForeground(UIStyle.getTextPrimary());
        card.add(msgLabel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        JButton cancelBtn = new JButton("취소");
        JButton deleteBtn = new JButton("삭제");

        styleFlatButton(cancelBtn);
        styleDangerButton(deleteBtn);

        cancelBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        deleteBtn.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        bottom.add(cancelBtn);
        bottom.add(deleteBtn);

        card.add(bottom, BorderLayout.SOUTH);
    }

    private void styleFlatButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        btn.setForeground(UIStyle.getTextSecondary());
        btn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
    }

    private void styleDangerButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setBackground(new Color(210, 70, 80));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btn.setOpaque(true);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    // 🔹 static 헬퍼: 어디서든 쉽게 호출
    public static boolean showDeleteConfirm(Component parent, String message) {
        Window w = SwingUtilities.getWindowAncestor(parent);
        Frame owner = (w instanceof Frame) ? (Frame) w : null;

        ConfirmDialog dialog = new ConfirmDialog(owner, "삭제 확인", message);
        dialog.setUndecorated(false);
        dialog.setVisible(true);   // 모달 → 여기서 블록됨

        return dialog.isConfirmed();
    }
}
