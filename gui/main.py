#!/usr/bin/python3
# -*- coding: utf-8 -*-
import sys
import os.path
import subprocess
from PyQt5.QtWidgets import QApplication
from PyQt5.QtCore import QCoreApplication
from PyQt5.QtCore import QCommandLineParser
from PyQt5.QtCore import QCommandLineOption
from PyQt5.QtCore import qInstallMessageHandler
from PyQt5.QtCore import QtInfoMsg
from PyQt5.QtCore import QtWarningMsg
from PyQt5.QtCore import QtCriticalMsg
from PyQt5.QtCore import QtFatalMsg
from PyQt5.Qt import PYQT_VERSION_STR
from main_window import MainWndow


def QuietMessageHandler(mode, context, message):
    if mode == QtWarningMsg:
        mode = 'WARNING'
    elif mode == QtCriticalMsg:
        mode = 'CRITICAL'
    elif mode == QtFatalMsg:
        mode = 'FATAL'
    else:
        return
    print('%s: %s (%s:%s:%s )' % (mode, message, os.path.basename(context.file), context.function, context.line))

# qDebug, qInfo, qWarning, qCritical, qFatal
def VerboseMessageHandler(mode, context, message):
    if mode == QtInfoMsg:
        mode = 'INFO'
    elif mode == QtWarningMsg:
        mode = 'WARNING'
    elif mode == QtCriticalMsg:
        mode = 'CRITICAL'
    elif mode == QtFatalMsg:
        mode = 'FATAL'
    else:
        mode = 'DEBUG'
    print('%s: %s (%s:%s:%s )' % (mode, message, os.path.basename(context.file), context.function, context.line))



if __name__ == '__main__':
    app = QApplication(sys.argv)
    QCoreApplication.setOrganizationName('nomagic')
    QCoreApplication.setOrganizationDomain('nomagic.de')
    QCoreApplication.setApplicationName('Feature GUI')
    QCoreApplication.setApplicationVersion('py:0.1 dev')

    # parse the command line parameters
    parser = QCommandLineParser()
    parser.setApplicationDescription('GUI for Feature')
    parser.addHelpOption()
    parser.addVersionOption()
    verboseLoggingOption = QCommandLineOption(['l', 'log'],
        'print much more messages on the command line.')
    parser.addOption(verboseLoggingOption)

    parser.process(app)

    # default configuration
    cfg = {'verboseLogging' : False}

    if parser.isSet(verboseLoggingOption):
        cfg['verboseLogging'] = True
        qInstallMessageHandler(VerboseMessageHandler)
        print('Python Version used is  %s !' % (sys.version))
        print('PyQt version: should be at least 5.12.3 and is %s !' % (PYQT_VERSION_STR))
        stdoutdata = subprocess.getoutput('git rev-parse HEAD')
        print('git revision: ' + stdoutdata) # git rev-parse HEAD
        stdoutdata = subprocess.getoutput('git diff --shortstat')
        print('git changes ' + stdoutdata)  # git diff --shortstat
    else:
        qInstallMessageHandler(QuietMessageHandler)

    # create the main window
    window = MainWndow(cfg)
    window.show()
    sys.exit(app.exec_())
