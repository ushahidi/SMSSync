#/usr/bin/python!
# -*- coding: utf-8 -*-

"""
/** 
 ** Copyright (c) 2011 Ushahidi Inc
 ** All rights reserved
 ** Contact: team@ushahidi.com
 ** Website: http://www.ushahidi.com
 ** 
 ** GNU Lesser General Public License Usage
 ** This file may be used under the terms of the GNU Lesser
 ** General Public License version 3 as published by the Free Software
 ** Foundation and appearing in the file LICENSE.LGPL included in the
 ** packaging of this file. Please review the following information to
 ** ensure the GNU Lesser General Public License version 3 requirements
 ** will be met: http://www.gnu.org/licenses/lgpl.html.	
 **	
 **
 ** If you have questions regarding the use of this file, please contact
 ** Ushahidi developers at team@ushahidi.com.
 ** 
 **/ 

    generates smssync.ushahidi.com web pages
"""

__author__ = "Henry Addo (henry@ushahidi.com)"
__version__ = "$Revision: 1.0 $"
__copyright__ = "Copyright (c) 2011 Ushahidi"
__license__ = "LGPL"

import csv
import os
import sys
import markdown
import time

from optparse import OptionParser

from datetime import date

#TODO:// figure out how to use markdown 
class BuildSite:
    
    def __init__(self,source,destination):
        self.source = source
        self.destination = destination
        self.parts_of_webpages = '../genwebsite/'

    def transform_markdown_from_string(self,mkdown):
        return markdown.markdown(mkdown)

    def transform_markdown_from_file(self,mkdown):
        input_file = codecs.open(mkdown, mode="r", encoding="utf-8")
        mkdown_text = input_file.read()
        
        return markdown.markdown(mkdown_text)

    def write_html_to_file(self,html_file,html_string):
        print "Writing file "+html_file
        output_file = open(html_file,'w')
        output_file.write(html_string)

        print "Succesffully Done!"

    def read_html_files(self,source):
        f = open(source,'r')
        content = f.read()
        f.close()
        
        return content

    def build_pages(self):
        """ build web pages for smssync.ushahidi.com """
        
        #read header html stuff
        header = self.read_html_files(self.parts_of_webpages+'site_header.html')

        #read html stuff for the main content
        body = self.read_html_files(self.source)
        mkdown_body = self.transform_markdown_from_string(body)
        #Get today's date
        d = date.today()

        #read footer html stuff
        footer = self.apply_filter('#Date','Generated: '+d.strftime("%d-%m-%Y"),
                self.read_html_files(self.parts_of_webpages+'site_footer.html'))
        
        #combine the different parts of the web page into a single one
        #TODO:// figure a better way of doing this.

        page = header + mkdown_body + footer

        mkdown = self.transform_markdown_from_string(page)
        
        #write content to a file
        self.write_html_to_file(self.destination,mkdown)
    
    def apply_filter(self,keyword, word, content):
        """ find and replace for stuff in the html tags """
        return content.replace(keyword,word)


def main(args,options,parser):
    if len(args) < 2:
        parser.print_usage()
        print "%s: " %(str(len(args)))
    else :
        build_site = BuildSite(args[0],args[1])
        if options.mkfile == True:
            build_site.transform_markdown_from_file(args)
        else:
            build_site.build_pages()
usage = "usage: %prog <markdown string> <destination file>"

parser = OptionParser(usage=usage)
parser.add_option("-f", "--mkfile",action="store_true", 
    help="generate html from markdown file", dest="mkfile")
parser.add_option("-t", "--mktext",dest="mktext",action="store_true",help="generate html from markdown text")
(options, args) = parser.parse_args()

if __name__ == "__main__":
    main(args,options,parser)
