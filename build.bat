@echo off
cmd /c gradlew build
psftp -b build.bat.psftp pi@10.58.92.6 -pw %1
plink -m build.bat.plink pi@10.58.92.6 -pw %1