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

from optparse import OptionParser

#from markdown import markdown.extensions.nl2br

class BuildSite:
    
    def __init__(self,source,destination):
        self.source = source
        self.destination = destination

    def transform_markdown_from_string(self,mkdown):
        print markdown.markdown(mkdown)

    def transform_markdown_from_file(self,mkdown):
        input_file = codecs.open(mkdown, mode="r", encoding="utf-8")
        mkdown_text = input_file.read()
        
        return markdown.markdown(mkdown_text)
    
    def write_html_to_file(self,html_file,html_string):
        output_file = codecs.open(html_file,"w", encoding="utf-8",
                errors="xmlcharrefreplace"
        )
        output_file.write(html_string)

    def read_html_files(self):
        f = open(self.source,'r')
        content = f.readLines()
        f.close()
        
        return content

def main(args,options,parser):
    if len(args) < 2:
        parser.print_usage()
        print "%s: " %(str(len(args)))
    else :
        build_site = BuildSite(args[0],args[0])
        if options.mkfile == True:
            build_site.transform_markdown_from_file(args)
        else:
            build_site.transform_markdown_from_string(args[0])
usage = "usage: %prog <markdown string> <destination file>"

parser = OptionParser(usage=usage)
parser.add_option("-f", "--mkfile",action="store_true", 
    help="generate html from markdown file", dest="mkfile")
parser.add_option("-t", "--mktext",dest="mktext",action="store_true",help="generate html from markdown text")
(options, args) = parser.parse_args()

if __name__ == "__main__":
    main(args,options,parser)
