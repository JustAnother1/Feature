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
from target import Target
from states import States
from connect_dialog import ConnectDialog
from window_debugger_monitor import WindowDebuggerMonitor
from window_source_code import WindowSourceCode
from window_source_files_list import SourceFilesListWindow
from window_program_output import ProgramOutputWindow
from session import ExecCmd


class MainWndow(QMainWindow):
    '''
    Main Window
    '''
    configurations = None
    state = States.not_configured
    stateLabel = None
    target = None
    stateListeners = []
    session = None
    
    DebuggerMonitorWindow = None
    srcWnd = None
    SourceFilesListWindow = None
    ProgramOutputWindow = None
    
    targetMenu = None
    connectAct = None
    disconnectAct = None
    quitAct = None
    
    debugMenu = None
    debugToolBar = None
    runAct = None
    pauseAct = None
    terminateAct = None
    stepIntoAct = None
    stepOverAct = None
    stepOutAct = None
    
    helpMenu = None
    aboutAct = None
    aboutQtAct = None
    

    def __init__(self, cfg, parent=None):
        super().__init__(parent)
        self.stateListeners.append(self)
        self.configurations = {}
        self.configurations['command line'] = cfg
        self.target = Target(self.configurations)
        self.DebuggerMonitorWindow = WindowDebuggerMonitor() 
        self.target.connectMonitor(self.DebuggerMonitorWindow)
        self.srcWnd = WindowSourceCode(self.configurations)
        self.SourceFilesListWindow = SourceFilesListWindow(self.configurations, self.srcWnd)
        self.ProgramOutputWindow = ProgramOutputWindow()
        
        self.configurations['personal'] = Configuration('native', None)
        self.resize(self.configurations['personal'].getInt('MainWindow.width', 750), self.configurations['personal'].getInt('MainWindow.height', 550) )
        self.setWindowIcon(QIcon('images/logo.png'))

        self.setWindowTitle('LDbg')
        
        self.createMenuAndToolbar()
        self.createStatusBar()
        self.createWindows()
        self.setWindowTitle("LDbg")
        
        self.switchState(States.not_configured)
        
        # if we had a target defined on the command line
        # -> try to connect to target
        if None != cfg['projectCfgFileName']:
            if 0 < len(cfg['projectCfgFileName']):
                self.configurations['project'] = Configuration('ini', cfg['projectCfgFileName'])
                if True == self.target.tryToConnectToConfiguration(self.configurations['project']):
                    self.session = self.target.getSession()
                    self.srcWnd.setSession(self.session )
                    self.SourceFilesListWindow.setSession(self.session )
                    self.switchState(States.connected)
        else:
            if None != cfg['binaryFileName']:
                if True == self.target.tryToConnectToBinary(cfg['binaryFileName']):
                    self.session  = self.target.getSession()
                    self.srcWnd.setSession(self.session )
                    self.SourceFilesListWindow.setSession(self.session)
                    self.switchState(States.connected)   
      
    def createMenuAndToolbar(self):
        # Target
        self.targetMenu = self.menuBar().addMenu('&Target')

        # - Connect
        self.connectAct = QAction('C&onnect', self)
        self.connectAct.setStatusTip('Connect to target')
        self.connectAct.setEnabled(True)
        self.connectAct.triggered.connect(self.connectToTarget)
        self.targetMenu.addAction(self.connectAct)

        # - Disconnect
        self.disconnectAct = QAction('&Disconnect', self)
        self.disconnectAct.setStatusTip('Disconnect from target')
        self.disconnectAct.setEnabled(False)
        self.disconnectAct.triggered.connect(self.disconnectFromTarget)
        self.targetMenu.addAction(self.disconnectAct)

        self.targetMenu.addSeparator()

        # - Quit
        self.quitAct = QAction('&Quit', self)
        self.quitAct.setShortcuts(QKeySequence.Quit)
        self.quitAct.setStatusTip('Quit the application')
        self.quitAct.triggered.connect(self.QuitLdbg)
        self.targetMenu.addAction(self.quitAct)

        # Debug
        self.debugMenu = self.menuBar().addMenu('&Debug')
        self.debugToolBar = self.addToolBar('tool bar')

        # - Run
        self.runAct = QAction(QIcon('images/play.png'), '&Run', self)
        self.runAct.setStatusTip('Resume execution')
        self.runAct.setEnabled(False)
        self.runAct.triggered.connect(self.runCmd)
        self.debugMenu.addAction(self.runAct)
        self.debugToolBar.addAction(self.runAct)

        # - Pause
        self.pauseAct = QAction(QIcon('images/pause.png'), '&Pause', self)
        self.pauseAct.setStatusTip('Pause execution')
        self.pauseAct.setEnabled(False)
        self.pauseAct.triggered.connect(self.pauseCmd)
        self.debugMenu.addAction(self.pauseAct)
        self.debugToolBar.addAction(self.pauseAct)

        # - Terminate
        self.terminateAct = QAction(QIcon('images/stop.png'), '&Terminate', self)
        self.terminateAct.setStatusTip('Terminate execution')
        self.terminateAct.setEnabled(False)
        self.terminateAct.triggered.connect(self.terminateCmd)
        self.debugMenu.addAction(self.terminateAct)
        self.debugToolBar.addAction(self.terminateAct)

        # - Step into
        self.stepIntoAct = QAction(QIcon('images/step_in.png'), '&Step into', self)
        self.stepIntoAct.setStatusTip('Step into function')
        self.stepIntoAct.setEnabled(False)
        self.stepIntoAct.triggered.connect(self.stepIntoCmd)
        self.debugMenu.addAction(self.stepIntoAct)
        self.debugToolBar.addAction(self.stepIntoAct)

        # - Step over
        self.stepOverAct = QAction(QIcon('images/step_over.png'), 'Step &Over', self)
        self.stepOverAct.setStatusTip('step over function')
        self.stepOverAct.setEnabled(False)
        self.stepOverAct.triggered.connect(self.stepOverCmd)
        self.debugMenu.addAction(self.stepOverAct)
        self.debugToolBar.addAction(self.stepOverAct)

        # - Step out
        self.stepOutAct = QAction(QIcon('images/step_out.png'), 'step ou&t', self)
        self.stepOutAct.setStatusTip('step out of this function')
        self.stepOutAct.setEnabled(False)
        self.stepOutAct.triggered.connect(self.stepOutCmd)
        self.debugMenu.addAction(self.stepOutAct)
        self.debugToolBar.addAction(self.stepOutAct)
    
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

        
    def connectToTarget(self):
        dialog = ConnectDialog(self.configurations)
    
        if QDialog.Accepted == dialog.exec():
            # Read data from dialog into project cfg
            self.configurations['project'] = dialog.getConfiguration()
            if True == self.target.tryToConnectToConfiguration(self.configurations['project']):
                activeSession = self.target.getSession()
                self.srcWnd.setSession(activeSession)
                self.SourceFilesListWindow.setSession(activeSession)
                self.switchState(States.connected)
            else:
                qDebug('Target not connected !')
    
    def disconnectFromTarget(self):
        if None != self.target:
            self.target.endSession()
        self.switchState(States.not_configured)
    
    def QuitLdbg(self):
        qDebug("Starting shutdown")
        self.switchState(States.end)
        QApplication.quit()    
    
    def about(self):
        QMessageBox.about(self, QApplication.applicationName() + " " + QApplication.applicationVersion(), 'This is a GUI for gdb.')      
        
    def createStatusBar(self):
        self.statusBar().showMessage('Ready')
        self.stateLabel = QLabel( States.not_configured.name)
        self.statusBar().addPermanentWidget(self.stateLabel, 0)
        
    def createWindows(self):
        # -Source Code
        self.setCentralWidget(self.srcWnd);
        self.stateListeners.append(self.srcWnd)        
    
        # -Watched Variables
        #WatchedWindow watchWnd(this);
        #addDockWidget(Qt::RightDockWidgetArea, &watchWnd);
        # -Local Variables
        # -Memory dump
        # -CPU Register (gdb:info all-registers)
        #CpuRegisterWindow cpuWnd(this);
        #addDockWidget(Qt::RightDockWidgetArea, &cpuWnd);
        # -Breakpoints (gdb:info breakpoints)
        
        # Output gdb
        self.addDockWidget(Qt.BottomDockWidgetArea, self.DebuggerMonitorWindow)
        self.stateListeners.append(self.DebuggerMonitorWindow)
        
        # -Stack Trace
        # -Peripheral Special Function Registers
        # -functions  (List : Name, address, size)
        # -Source file list
        self.addDockWidget(Qt.LeftDockWidgetArea, self.SourceFilesListWindow)
        self.stateListeners.append(self.SourceFilesListWindow)
        # -disassembly(of memory region)
        # -output of program
        self.addDockWidget(Qt.BottomDockWidgetArea, self.ProgramOutputWindow)
        # -threads
        # -memory region
        
        
    def changeStateNotification(self, oldState, newState):
        if newState ==  States.not_configured:
            self.stepOutAct.setEnabled(False)
            self.stepOverAct.setEnabled(False)
            self.stepIntoAct.setEnabled(False)
            self.terminateAct.setEnabled(False)
            self.pauseAct.setEnabled(False)
            self.runAct.setEnabled(False)
            self.disconnectAct.setEnabled(False)
            self.connectAct.setEnabled(True)
            
        elif newState == States.connected:
            self.stepOutAct.setEnabled(False)
            self.stepOverAct.setEnabled(False)
            self.stepIntoAct.setEnabled(False)
            self.terminateAct.setEnabled(False)
            self.pauseAct.setEnabled(False)
            self.runAct.setEnabled(True)
            self.disconnectAct.setEnabled(True)
            self.connectAct.setEnabled(False)
            
        elif newState == States.running or newState == States.stepping:
            self.stepOutAct.setEnabled(False)
            self.stepOverAct.setEnabled(False)
            self.stepIntoAct.setEnabled(False)
            self.terminateAct.setEnabled(True)
            self.pauseAct.setEnabled(True)
            self.runAct.setEnabled(False)
            self.disconnectAct.setEnabled(True)
            self.connectAct.setEnabled(False)
            
        elif newState == States.halted:
            self.stepOutAct.setEnabled(True)
            self.stepOverAct.setEnabled(True)
            self.stepIntoAct.setEnabled(True)
            self.terminateAct.setEnabled(True)
            self.pauseAct.setEnabled(False)
            self.runAct.setEnabled(True)
            self.disconnectAct.setEnabled(True)
            self.connectAct.setEnabled(False)            
            
        elif newState ==  States.end:    
            self.stepOutAct.setEnabled(False)
            self.stepOverAct.setEnabled(False)
            self.stepIntoAct.setEnabled(False)
            self.terminateAct.setEnabled(False)
            self.pauseAct.setEnabled(False)
            self.runAct.setEnabled(False)
            self.disconnectAct.setEnabled(False)
            self.connectAct.setEnabled(False)                    
            self.configurations['personal'].setInt('MainWindow.width', self.width())
            self.configurations['personal'].setInt('MainWindow.height', self.height())
            self.configurations['personal'].writeToFile();
            if None != self.target:
                self.target.endSession()
            self.target = None     
            
            
            
        self.stateLabel.setText(str(newState))        
        
    def switchState(self, newState):
        # react to the states
        if self.state == newState:
            return # no change
        
        # housekeeping
        for listener in self.stateListeners:
            listener.changeStateNotification(self.state, newState) # notify everybody

        self.state = newState # switch to new state
        
    def closeEvent(self, event):
        qDebug("Starting shutdown after event")
        self.switchState(States.end)
        QApplication.quit()   
        event.accept()
        
    def runCmd(self):
        if self.state == States.connected:
            self.session.commandExecution(ExecCmd.run_start)
            self.switchState(States.running)
        elif self.state == States.halted:
            self.session.commandExecution(ExecCmd.run_continue)
            self.switchState(States.running) 
    
    def pauseCmd(self):
        self.session.commandExecution(ExecCmd.pause)
        self.switchState(States.halted) 
    
    def terminateCmd(self):
        self.session.commandExecution(ExecCmd.stop)
        self.switchState(States.halted)
    
    def stepIntoCmd(self):
        self.session.commandExecution(ExecCmd.stepIn)
        self.switchState(States.stepping)
    
    def stepOverCmd(self):
        self.session.commandExecution(ExecCmd.stepOver)
        self.switchState(States.stepping)
    
    def stepOutCmd(self):
        self.session.commandExecution(ExecCmd.stepOut)
        self.switchState(States.stepping)
        
    #EOF