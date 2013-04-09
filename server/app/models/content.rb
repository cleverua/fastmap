class Content < ActiveRecord::Base
  attr_accessible :lat, :lng, :qtree_index, :title

  validates :lat, :numericality => {:greater_than_or_equal_to => -90, :less_than_or_equal_to => 90 }, :allow_nil => true
  validates :lng, :numericality => {:greater_than_or_equal_to => -180, :less_than_or_equal_to => 180 }, :allow_nil => true

  TWO_IN_POWER_30 = 2**30


  before_save :update_qtree_index
  after_save :delete_cache
  after_destroy :delete_cache


  def self.qtree_index(lat, lng)
    sin_lat = Math.sin(lat.to_f * Math::PI / 180)
    yy = 0.5 - Math.log((1+sin_lat) / (1-sin_lat)) / (4 * Math::PI)

    lat_q = (yy * TWO_IN_POWER_30).to_i.to_s(2).rjust(30,'0')
    lng_q = ((lng + 180).to_f / 360 * TWO_IN_POWER_30).to_i.to_s(2).rjust(30,'0')

    puts "lat_q = #{lat_q}"
    puts "lng_q = #{lng_q}"

    lat_array = lat_q.chars.to_a
    lng_array = lng_q.chars.to_a

    q_index = ""
    lat_array.each_with_index do |element, index|
      digit = 0
      digit += 2 if element == "1"
      digit += 1 if lng_array[index] == "1"

      q_index << digit.to_s  # 4
    end

    puts "q_index = #{q_index}"

    return q_index
  end

  private

  def update_qtree_index
    if self.lat_changed? || self.lng_changed? || self.qtree_index.blank? #|| self.deleted_changed?
      begin
        self.qtree_index = Content.qtree_index(self.lat, self.lng)
      rescue => e
        logger.warn "-----------------Exeption while update_qtree_index-------------------"
        logger.warn e.inspect
      end
    end
  end

  def delete_cache
    if self.lat_changed? || self.lng_changed?  #|| self.deleted_changed?
      delete_cache_by_qtree_index unless self.qtree_index.blank?
    end
  end

  def delete_cache_by_qtree_index
    key = ''
    self.qtree_index.each_char do |char|
      key << char
      Rails.cache.delete("/rest/v2.0/maps/#{key}.json")
    end
  end

end
