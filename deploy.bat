@echo off
cmd /c gradlew build
psftp -b deploy.bat.psftp pi@10.58.92.6 -pw %RASPI_PASSWD%
plink -m deploy.bat.plink pi@10.58.92.6 -pw %RASPI_PASSWD%