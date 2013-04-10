# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
# Examples:
#
#   cities = City.create([{ name: 'Chicago' }, { name: 'Copenhagen' }])
#   Mayor.create(name: 'Emanuel', city: cities.first)


### Contents


Content.destroy_all

folder_path = File.dirname(__FILE__)+'/poi/*'
csv_file_list = Dir.glob(folder_path)

if Content.count.zero?
  csv_file_list.each do |csv_file|
    File.open(csv_file).each_line do |line|
      if line=~/(.+),(.+),(.+),(.+)/
        csv = line.match(/(.+),(.+),(.+),(.+)/)
        Content.create!(
            title: csv[4],
            lat: csv[2],
            lng: csv[1]
        )
        puts 'poi: '+csv[3]+' - created.'
      end
    end
  end

  puts "Contents created"
else
  puts "Contents exists"
end
