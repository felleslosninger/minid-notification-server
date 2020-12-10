class minid_notification_server::test_setup inherits minid_notification_server{

  include platform
  include wget
  if ($platform::test_setup) {

    wget::fetch { 'download_firebase-credentials':
      source             => 'http://static.dmz.local/vagrant/eid/resources/firebase-credentials.json',
      destination        => "${minid_notification_server::config_dir}${minid_notification_server::application}/firebase-credentials.json",
      nocheckcertificate => true,
    }
  }
}
