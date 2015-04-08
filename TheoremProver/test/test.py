import os
import re
COMMAND = "python ./src/main.py ./test/"
for path in os.listdir(os.getcwd()+"/test/"):
    if re.match('.*\.in$', path):
        tempCmd = COMMAND + path
        print 'TEST %(file)s' % {"file": path}
        os.system(tempCmd)
        print