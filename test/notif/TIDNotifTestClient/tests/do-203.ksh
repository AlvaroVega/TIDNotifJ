if [ .$1. == .. ]
then
  echo
  echo "usage:"
  echo "  do-203.ksh <num.test>"
  echo
  exit 0
fi

echo
echo "do-203.ksh" $1
echo

echo
run-serie-200.ksh $1 &
sleep 5
echo test.ksh $1
test.ksh $1
theend.ksh
echo done.

