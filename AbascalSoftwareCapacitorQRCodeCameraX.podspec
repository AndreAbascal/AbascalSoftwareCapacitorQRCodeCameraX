
  Pod::Spec.new do |s|
    s.name = 'AbascalSoftwareCapacitorQRCodeCameraX'
    s.version = '0.0.1'
    s.summary = 'CapacitorQRCodeCameraX'
    s.license = 'MIT'
    s.homepage = 'https://github.com/AndreAbascal/AbascalSoftwareCapacitorQRCodeCameraX.git'
    s.author = 'Abascal Software'
    s.source = { :git => 'https://github.com/AndreAbascal/AbascalSoftwareCapacitorQRCodeCameraX.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end