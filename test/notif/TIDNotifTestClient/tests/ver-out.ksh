if [ .$1. == .. ]
then
  echo
  echo "usage:"
  echo "  ver-out.ksh <num.test>"
  echo
  exit 0
fi

echo \*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*
echo \* tidnotif-$1.out \*
echo \*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*\*
echo

grep "\*\* Num. Processed Events" OUT/tidnotif-$1.out
grep "\*\* Average Processing Ratio" OUT/tidnotif-$1.out
grep "\*\* Average Processing Time" OUT/tidnotif-$1.out
grep "\*\* Max. Processing Time" OUT/tidnotif-$1.out
grep "\*\* Total Processing Time" OUT/tidnotif-$1.out
echo

grep "++ Num. Scheduled Events" OUT/tidnotif-$1.out
grep "++ Average Scheduling Ratio" OUT/tidnotif-$1.out
grep "++ Average Scheduling Time" OUT/tidnotif-$1.out
grep "++ Max. Scheduling Time" OUT/tidnotif-$1.out
grep "++ Total Scheduling Time" OUT/tidnotif-$1.out
echo

grep "NO PROCESSED EVENTS" OUT/tidnotif-$1.out
echo
