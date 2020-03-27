from PyQt5.QtWidgets import QDialog
from PyQt5.QtCore import qDebug
from PyQt5.QtWidgets import QDialogButtonBox
from PyQt5.QtWidgets import QVBoxLayout

class SettingsDialog(QDialog):

    settings = None

    def __init__(self, *args, **kwargs):
        super(SettingsDialog, self).__init__(*args, **kwargs)
        self.setWindowTitle("Edit settings")
        QBtn = QDialogButtonBox.Ok | QDialogButtonBox.Cancel

        self.buttonBox = QDialogButtonBox(QBtn)
        self.buttonBox.accepted.connect(self.accept)
        self.buttonBox.rejected.connect(self.reject)

        self.layout = QVBoxLayout()
        self.layout.addWidget(self.buttonBox)
        self.setLayout(self.layout)

    def addSettings(self, newSettings):
        self.settings = newSettings

    def getEditedSettings(self):
        return self.settings



# called from main window
def showSettingsDialog(wnd, cfg):
    qDebug("schowing Settings")
    dlg = SettingsDialog(wnd)

    if dlg.exec_():
        qDebug("Success!")
        return dlg.getEditedSettings()
    else:
        qDebug("Cancel!")
        return cfg
