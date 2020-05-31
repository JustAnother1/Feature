from PyQt5.QtWidgets import QMainWindow
from PyQt5.QtWidgets import QAction
from PyQt5.QtWidgets import QLabel
from PyQt5.QtWidgets import QDialog
from PyQt5.QtWidgets import QMessageBox
from PyQt5.QtWidgets import QApplication
from PyQt5.QtGui import QIcon
from PyQt5.QtGui import QKeySequence
from PyQt5.QtCore import Qt
from PyQt5.QtCore import qDebug
from configuration import Configuration

from settings import showSettingsDialog


class MainWndow(QMainWindow):
    '''
    Main Window
    '''
    configurations = None

    fileMenu = None
    settingsAct = None
    quitAct = None

    helpMenu = None
    aboutAct = None
    aboutQtAct = None

    def __init__(self, cfg, parent=None):
        super().__init__(parent)

        self.configurations = {}
        self.configurations['command line'] = cfg
        self.configurations['bla'] = cfg
        self.configurations['personal'] = Configuration('native', None)

        self.resize(self.configurations['personal'].getInt('MainWindow.width', 750), self.configurations['personal'].getInt('MainWindow.height', 550) )
        self.setWindowIcon(QIcon('images/logo.png'))

        self.createMenuAndToolbar()
        self.createStatusBar()
        self.setWindowTitle("Feature GUI")


    def createMenuAndToolbar(self):
        # Target
        self.fileMenu = self.menuBar().addMenu('&File')

        self.settingsAct = QAction('&Settings', self)
        self.settingsAct.setStatusTip('Show the settings')
        self.settingsAct.triggered.connect(self.SettingsDialog)
        self.fileMenu.addAction(self.settingsAct)

        self.fileMenu.addSeparator()

        # - Quit
        self.quitAct = QAction('&Quit', self)
        self.quitAct.setShortcuts(QKeySequence.Quit)
        self.quitAct.setStatusTip('Quit the application')
        self.quitAct.triggered.connect(self.QuitLdbg)
        self.fileMenu.addAction(self.quitAct)

        # Help
        self.helpMenu = self.menuBar().addMenu('&Help')

        # - about
        self.aboutAct = QAction('&About', self)
        self.aboutAct.setStatusTip("Show the application's About box")
        self.aboutAct.triggered.connect(self.about)
        self.helpMenu.addAction(self.aboutAct)

        # - aboutQt
        self.aboutQtAct = QAction('About &Qt', self)
        self.aboutQtAct.setStatusTip("Show the Qt library's About box")
        self.aboutQtAct.triggered.connect(QApplication.aboutQt)
        self.helpMenu.addAction(self.aboutQtAct)

    def SettingsDialog(self):
        self.configurations = showSettingsDialog(self, self.configurations)

    def QuitLdbg(self):
        qDebug("Starting shutdown")
        QApplication.quit()

    def about(self):
        QMessageBox.about(self, QApplication.applicationName() + " " + QApplication.applicationVersion(), 'This is a GUI for Feature. Feature is a system to create Software projects.')

    def createStatusBar(self):
        self.statusBar().showMessage('Ready')

    def closeEvent(self, event):
        qDebug("Starting shutdown after event")
        QApplication.quit()
        event.accept()

