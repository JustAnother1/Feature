from PyQt5.QtWidgets import QDialog
from PyQt5.QtCore import qDebug
from PyQt5.QtWidgets import QDialogButtonBox
from PyQt5.QtWidgets import QVBoxLayout
from PyQt5.QtWidgets import QScrollArea
from PyQt5.QtWidgets import QTabWidget
from PyQt5.QtWidgets import QGroupBox
from PyQt5.QtWidgets import QLabel
from PyQt5.QtWidgets import QPushButton
from PyQt5.QtWidgets import QFormLayout
from PyQt5.QtWidgets import QLineEdit
from PyQt5.QtWidgets import QWidget

class SettingsTab(QWidget):

    layout = None
    labelLis = []
    editList = []

    def __init__(self, *args, **kwargs):
        super(SettingsTab, self).__init__(*args, **kwargs)


    def addSettings(self, settings):
        self.layout = QFormLayout()

        self.allSettings = settings.getAllSettings()
        for i in range(len(self.allSettings)):
            print('i = ' + str(i))
            print('setting = ' + self.allSettings[i])
            print('value = ' + settings.getString(self.allSettings[i]))
            self.labelLis.append(QLabel(self.allSettings[i] + ' :'))
            self.editList.append(QLineEdit(self))
            self.editList[i].setText(settings.getString(self.allSettings[i]))
            self.editList[i].textChanged.connect(lambda chnaged,i = i: self.onChanged(i))
            self.layout.addRow(self.labelLis[i], self.editList[i])

        self.setLayout(self.layout)

    def onChanged(self, idx):
        print('change on '  + str(idx))


class SettingsDialog(QDialog):

    settings = None
    buttonBox = None
    scroll = None
    tabPane = None
    tabs = {}

    def __init__(self, *args, **kwargs):
        super(SettingsDialog, self).__init__(*args, **kwargs)
        self.setWindowTitle("Edit settings")

        QBtn = QDialogButtonBox.Ok | QDialogButtonBox.Cancel
        self.buttonBox = QDialogButtonBox(QBtn)
        self.buttonBox.accepted.connect(self.accept)
        self.buttonBox.rejected.connect(self.reject)

        self.tabPane = QTabWidget()

        self.scroll = QScrollArea()
        self.scroll.setWidget(self.tabPane)
        self.scroll.setWidgetResizable(True)

        layout = QVBoxLayout(self)
        layout.addWidget(self.scroll)
        layout.addWidget(self.buttonBox)
        self.setLayout(layout)

    def addSettings(self, newSettings):
        if None == newSettings:
            return
        self.settings = newSettings
        idx = 0
        for type in self.settings.keys():
            print('type = ' + type)
            self.tabs[type] = SettingsTab()
            self.tabs[type].addSettings(self.settings[type])
            self.tabPane.insertTab(idx, self.tabs[type], type)
            idx = idx + 1

    def getEditedSettings(self):
        for i in range(len(self.allSettings)):
            self.settings.setString(self.allSettings[i], self.editList[i].text())
        return self.settings

# called from main window
def showSettingsDialog(wnd, cfg):
    qDebug("showing Settings")
    dlg = SettingsDialog(wnd)
    dlg.addSettings(cfg)
    res = cfg

    if dlg.exec_():
        qDebug("Success!")
        res = dlg.getEditedSettings()
    else:
        qDebug("Cancel!")

    return res
