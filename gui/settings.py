from PyQt5.QtWidgets import QDialog
from PyQt5.QtCore import qDebug
from PyQt5.QtWidgets import QDialogButtonBox
from PyQt5.QtWidgets import QVBoxLayout
from PyQt5.QtWidgets import QScrollArea
from PyQt5.QtWidgets import QGroupBox
from PyQt5.QtWidgets import QLabel
from PyQt5.QtWidgets import QPushButton
from PyQt5.QtWidgets import QFormLayout
from PyQt5.QtWidgets import QLineEdit


class SettingsDialog(QDialog):

    settings = None
    formLayout = None
    buttonBox = None
    labelLis = []
    editList = []

    def __init__(self, *args, **kwargs):
        super(SettingsDialog, self).__init__(*args, **kwargs)
        self.setWindowTitle("Edit settings")
        QBtn = QDialogButtonBox.Ok | QDialogButtonBox.Cancel

        self.buttonBox = QDialogButtonBox(QBtn)
        self.buttonBox.accepted.connect(self.accept)
        self.buttonBox.rejected.connect(self.reject)

        self.formLayout = QFormLayout()
        groupBox = QGroupBox("Personal Settings")
        groupBox.setLayout(self.formLayout)
        scroll = QScrollArea()
        scroll.setWidget(groupBox)
        scroll.setWidgetResizable(True)
        scroll.setFixedHeight(400)
        layout = QVBoxLayout(self)
        layout.addWidget(scroll)
        layout.addWidget(self.buttonBox)
        self.setLayout(layout)

    def addSettings(self, newSettings):
        if None == newSettings:
            return
        self.settings = newSettings
        allSettings = newSettings.getAllSettings()
        for i in range(len(allSettings)):
            self.labelLis.append(QLabel(allSettings[i]))
            #self.editList.append(QPushButton("Click Me"))
            qle = QLineEdit(self)
#            qle.textChanged[str].connect(self.onChanged)
            qle.setText(self.settings.getString(allSettings[i]))
            self.editList.append(qle)
            self.formLayout.addRow(self.labelLis[i], self.editList[i])

    def getEditedSettings(self):
        return self.settings

#    def onChanged(self, text):
#        self.lbl.setText(text)
#        self.lbl.adjustSize()

# called from main window
def showSettingsDialog(wnd, cfg):
    qDebug("schowing Settings")
    dlg = SettingsDialog(wnd)
    dlg.addSettings(cfg)

    if dlg.exec_():
        qDebug("Success!")
        return dlg.getEditedSettings()
    else:
        qDebug("Cancel!")
        return cfg
