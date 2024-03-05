for i in {24..24}
do
	cd /mnt/hgfs/expdata/PDA-trace
	rm -r /mnt/hgfs/expdata/PDA-trace/info/chart/chart_${i}
	timeout 15m java -jar PDA-1.0-SNAPSHOT-runnable.jar trace -dir /mnt/hgfs/expdata/d4j_src -name chart -id ${i}
done
exit
