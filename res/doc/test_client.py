#!/usr/bin/python3
# -*- coding: utf-8 -*-

import xmlrpc.client

# documenatation about docuwiki API:
# https://www.dokuwiki.org/devel:xmlrpc


# server = xmlrpc.client.ServerProxy('http://localhost/lib/exe/xmlrpc.php', verbose=True)
#server = xmlrpc.client.ServerProxy('http://localhost/lib/exe/xmlrpc.php')
#server = xmlrpc.client.ServerProxy('http://192.168.178.24/lib/exe/xmlrpc.php')
server = xmlrpc.client.ServerProxy('https://wiki.ing-poetter.de/lib/exe/xmlrpc.php')

print('Dokuwiki Version:', server.dokuwiki.getVersion())
print('TimeStamp:', server.dokuwiki.getTime())
print('API Version:', server.dokuwiki.getXMLRPCAPIVersion())
print('Wiki Title:', server.dokuwiki.getTitle())
#print('Wiki page start:', server.wiki.getPage("start"))
#print('Wiki page list:', server.dokuwiki.getPagelist("", ""))

#print('Wiki page start:', server.wiki.getPage("spe:stm32f767_cubemx"))
