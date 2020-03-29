from PyQt5.QtCore import QSettings

class Configuration(object):
    '''
    Target / Project configuration
    '''
    filename = ''
    data = None

    def __init__(self, cfg_type, source):
        '''
        Constructor
        '''
        self.filename = source
        if 'native' == cfg_type:
            self.data = QSettings(QSettings.NativeFormat, QSettings.UserScope, 'nomagic', 'Feature')
        elif 'ini'  == cfg_type:
            self.data = QSettings(source, QSettings.IniFormat)
        else:
            self.data = None


    def writeToFile(self):
        if None != self.data:
            self.data.sync()

    def getInt(self, settingName, default):
        if None != self.data:
            if None != settingName:
                res = self.data.value(settingName, defaultValue=default, type=int)
                if res == default:
                    self.data.setValue(settingName, res)
                return res
        return default

    def setInt(self, which, value):
        if None != self.data:
            self.data.setValue(which, value)

    def setString(self, which, value):
        if None != self.data:
            self.data.setValue(which, value)

    def getString(self, which, defaultValue = ''):
        if None == self.data:
            return defaultValue
        res = str(self.data.value(which, defaultValue))
        if res == defaultValue:
            self.data.setValue(which, defaultValue)
        return res

    def getAllSettings(self):
        if None == self.data:
            return []
        return self.data.allKeys()
