/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GUI.java
 *
 * Created on Dec 23, 2009, 2:57:25 AM
 */

package hr.fer.zemris.aa.main;

import hr.fer.zemris.aa.features.FeatureClass;
import hr.fer.zemris.aa.features.FeatureGenerator;
import hr.fer.zemris.aa.features.IFeatureExtractor;
import hr.fer.zemris.aa.features.impl.ComboFeatureExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordFreqExtractor;
import hr.fer.zemris.aa.features.impl.FunctionWordOccurNumExtractor;
import hr.fer.zemris.aa.features.impl.PunctuationMarksExtractor;
import hr.fer.zemris.aa.recognizers.AuthorRecognizer;
import hr.fer.zemris.aa.recognizers.RecognizerTrainer;
import hr.fer.zemris.aa.recognizers.impl.LibsvmRecognizer;
import hr.fer.zemris.aa.xml.XMLMiner;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ivan444
 */
public class GUI extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;
	
	/** Creates new form GUI */
    public GUI() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblLearnedModelPath = new javax.swing.JLabel();
        txtLearnedModelPath = new javax.swing.JTextField();
        butLearnedModelPathJFC = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtTextToClassify = new javax.swing.JTextArea();
        butClassify = new javax.swing.JButton();
        lblRecogStatus = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblTrainDataPath = new javax.swing.JLabel();
        txtTrainDataPath = new javax.swing.JTextField();
        butTrainDataPathJFC = new javax.swing.JButton();
        lblSaveModelPath = new javax.swing.JLabel();
        txtSaveModelPath = new javax.swing.JTextField();
        butSaveModelPathJFC = new javax.swing.JButton();
        butStartTraining = new javax.swing.JButton();
        lblTrainingStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Prepoznavanje autora teksta");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLocationByPlatform(true);

        lblLearnedModelPath.setText("Putanja do modela klasifikatora:");

        txtLearnedModelPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLearnedModelPathActionPerformed(evt);
            }
        });
        txtLearnedModelPath.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtLearnedModelPathInputMethodTextChanged(evt);
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
        });

        butLearnedModelPathJFC.setText("Odaberi");
        butLearnedModelPathJFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butLearnedModelPathJFCActionPerformed(evt);
            }
        });

        jLabel1.setText("Tekst kojem treba prepoznati autora:");

        txtTextToClassify.setColumns(20);
        txtTextToClassify.setRows(5);
        jScrollPane2.setViewportView(txtTextToClassify);

        butClassify.setText("Prepoznaj");
        butClassify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClassifyActionPerformed(evt);
            }
        });

        lblRecogStatus.setText("Putanja do modela nije definirana.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addComponent(lblLearnedModelPath, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtLearnedModelPath, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butLearnedModelPathJFC))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(butClassify)
                        .addGap(18, 18, 18)
                        .addComponent(lblRecogStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblLearnedModelPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLearnedModelPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butLearnedModelPathJFC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butClassify)
                    .addComponent(lblRecogStatus))
                .addGap(12, 12, 12))
        );

        jTabbedPane1.addTab("Prepoznavanje", jPanel1);

        lblTrainDataPath.setText("Putanja do datoteke sa primjerima za učenje:");

        butTrainDataPathJFC.setText("Odaberi");
        butTrainDataPathJFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTrainDataPathJFCActionPerformed(evt);
            }
        });

        lblSaveModelPath.setText("Putanja na koju treba spremiti naučeni model:");

        txtSaveModelPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSaveModelPathActionPerformed(evt);
            }
        });

        butSaveModelPathJFC.setText("Odaberi");
        butSaveModelPathJFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveModelPathJFCActionPerformed(evt);
            }
        });

        butStartTraining.setText("Pokreni učenje");
        butStartTraining.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butStartTrainingActionPerformed(evt);
            }
        });

        lblTrainingStatus.setForeground(new java.awt.Color(255, 21, 0));
        lblTrainingStatus.setText("Putanja do datoteke s primjerima nije definirana.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTrainingStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addComponent(lblTrainDataPath)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(txtTrainDataPath, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butTrainDataPathJFC))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtSaveModelPath)
                            .addComponent(lblSaveModelPath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(8, 8, 8)
                        .addComponent(butSaveModelPathJFC))
                    .addComponent(butStartTraining))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTrainDataPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTrainDataPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butTrainDataPathJFC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSaveModelPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSaveModelPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butSaveModelPathJFC))
                .addGap(18, 18, 18)
                .addComponent(butStartTraining)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addComponent(lblTrainingStatus)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Učenje", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>

    private void txtSaveModelPathActionPerformed(java.awt.event.ActionEvent evt) {
    }                                                

    private void txtLearnedModelPathActionPerformed(java.awt.event.ActionEvent evt) {                                                    
    }                                                   
    
    private List<FeatureClass> loadTrainData(String trainDataPath, IFeatureExtractor extractor) {
		FeatureGenerator fg = new FeatureGenerator(extractor);
		XMLMiner miner = new XMLMiner(trainDataPath);
		
		return fg.generateFeatureVectors(miner);
	}
    
    private void butStartTrainingActionPerformed(java.awt.event.ActionEvent evt) {
        if (txtTrainDataPath.getText() == null || txtTrainDataPath.getText().equals("")) {
        	lblTrainingStatus.setText("Putanja do podataka za učenje nije definirana!");
        	return;
        } else if (txtSaveModelPath.getText() == null || txtSaveModelPath.getText().equals("")) {
        	lblTrainingStatus.setText("Putanja za spremanje naučenog modela nije definirana!");
        	return;
        }
        
        // TODO: Staviti odgovarajući featureExt
        lblTrainingStatus.setText("Učenje je u tijeku!");
        IFeatureExtractor featExtrac = new ComboFeatureExtractor(
				new PunctuationMarksExtractor(new File("config/marks.txt")),
				new FunctionWordOccurNumExtractor(new File("config/fwords.txt"))
		);
		RecognizerTrainer trainer = new LibsvmRecognizer(featExtrac);
		try {
			List<FeatureClass> trainData = loadTrainData(txtTrainDataPath.getText(), featExtrac);
			trainer.train(trainData, txtSaveModelPath.getText());
		} catch (Exception e) {
			lblTrainingStatus.setText("Dogodila se greška pri učenju!");
			return;
		}
		lblTrainingStatus.setText("Učenje je završeno!");
    }

    private void butSaveModelPathJFCActionPerformed(java.awt.event.ActionEvent evt) {
    	JFileChooser jfc = new JFileChooser(".");
        jfc.setFileFilter(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() ||
					   pathname.getAbsolutePath().toLowerCase().endsWith(".model");
			}

			@Override
			public String getDescription() {
				return "libsvm model (.model)";
			}
		});
        
        int returnVal = jfc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txtTrainDataPath.setText(jfc.getSelectedFile().getAbsolutePath());
        }
    }

    private void butTrainDataPathJFCActionPerformed(java.awt.event.ActionEvent evt) {
    	JFileChooser jfc = new JFileChooser(".");
        jfc.setFileFilter(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() ||
					   pathname.getAbsolutePath().toLowerCase().endsWith(".xml");
			}

			@Override
			public String getDescription() {
				return "XML train data (.xml)";
			}
		});
        
        int returnVal = jfc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txtTrainDataPath.setText(jfc.getSelectedFile().getAbsolutePath());
        }
    }

    private void butLearnedModelPathJFCActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser jfc = new JFileChooser(".");
        jfc.setFileFilter(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() ||
					   pathname.getAbsolutePath().toLowerCase().endsWith(".model");
			}

			@Override
			public String getDescription() {
				return "libsvm model (.model)";
			}
		});
        
        int returnVal = jfc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txtLearnedModelPath.setText(jfc.getSelectedFile().getAbsolutePath());
        }

    }

    private void butClassifyActionPerformed(java.awt.event.ActionEvent evt) {
    	if (txtTextToClassify.getText() == null || txtTextToClassify.getText().equals("")) {
    		lblRecogStatus.setText("Nije zadan tekst!");
    		return;
    	} else if (txtLearnedModelPath.getText() == null || txtLearnedModelPath.getText().equals("")) {
    		lblRecogStatus.setText("Putanja do modela nije definirana!");
    		return;
    	}
    	
    	// TODO: Staviti odgovarajući featureExt
    	AuthorRecognizer recognizer = null;
    	try {
    		IFeatureExtractor featExtrac = new ComboFeatureExtractor(
    				new PunctuationMarksExtractor(new File("config/marks.txt")),
    				new FunctionWordOccurNumExtractor(new File("config/fwords.txt"))
    		);
    		recognizer = new LibsvmRecognizer(txtLearnedModelPath.getText(), featExtrac);
    	} catch (Exception e) {
    		lblRecogStatus.setText("Dogodila se greška pri klasifikaciji!");
    		return;
    	}
		
		lblRecogStatus.setText("Autor teksta je: " + recognizer.classifyAutor(txtTextToClassify.getText()));
    }

    private void txtLearnedModelPathInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        if (txtLearnedModelPath.getText() == null || txtLearnedModelPath.getText().equals("")) {
            lblRecogStatus.setText("Putanja do modela nije definirana.");
        } else {
            lblRecogStatus.setText("Putanja do modela je definirana.");
        }
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton butClassify;
    private javax.swing.JButton butLearnedModelPathJFC;
    private javax.swing.JButton butSaveModelPathJFC;
    private javax.swing.JButton butStartTraining;
    private javax.swing.JButton butTrainDataPathJFC;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblLearnedModelPath;
    private javax.swing.JLabel lblRecogStatus;
    private javax.swing.JLabel lblSaveModelPath;
    private javax.swing.JLabel lblTrainDataPath;
    private javax.swing.JLabel lblTrainingStatus;
    private javax.swing.JTextField txtLearnedModelPath;
    private javax.swing.JTextField txtSaveModelPath;
    private javax.swing.JTextArea txtTextToClassify;
    private javax.swing.JTextField txtTrainDataPath;
    // End of variables declaration
}
