package spark_etl

// FIXME: workaround for dodgy sbt target, which doesn't take params.
object MainSbt extends App {
  Main.main(Array("-Denv.env=__ignore__", "validate-local"))
}
