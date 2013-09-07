if [ .$1. == .. ]
then
  echo
  echo "usage:"
  echo "  do.ksh <num.test>"
  echo
  exit 0
fi

echo
echo start: do $1

echo
echo test.ksh $1
test.ksh $1
echo done.

