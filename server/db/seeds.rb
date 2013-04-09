# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)


### Contents

Content.destroy_all

if Content.count.zero?
  Content.create!(
      title: "test1",
      lat: 32.962352,
      lng: -117.26807
  )
  Content.create!(
      title: "test2",
      lat: 45.962352,
      lng: -110.06807
  )
end
