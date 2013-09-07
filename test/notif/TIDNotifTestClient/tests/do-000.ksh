if [ .$1. == .. ]
then
  echo
  echo "usage:"
  echo "  do-000.ksh <num.test>"
  echo
  exit 0
fi

echo
echo "do-000.ksh" $1

echo
run-serie-000.ksh $1 &
sleep 5
echo test.ksh $1
test.ksh $1
theend.ksh
echo done.

