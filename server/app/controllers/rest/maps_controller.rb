class Rest::MapsController < ApplicationController

  before_filter :load_content

  QTREE_GROUPS = 4
  MAX_GROUPS_ZOOM_LEVEL = 11
  MIN_CLUSTER_SIZE = 6 # will not create cluster for elements less that MIN_CLUSTER_SIZE


  def show
    respond_to do |format|
      format.json {
        Rails.cache.write(request.path, render_to_string(json: {status: "ok", content: @content}), raw: true)
        render json: {status: "ok", content: @content}

      }
    end
  end
  private

  def load_content
    contents = Content.where("qtree_index like ?", "#{params[:id]}%")
    qtree = params[:id].to_s
    if qtree.length <= MAX_GROUPS_ZOOM_LEVEL
      clusters = Content.select("min(id) as id, avg(lat) as lat, avg(lng) as lng, count(*) as point_count, substring(qtree_index, 1, #{qtree.size} + #{QTREE_GROUPS}) as qtree_group, min(lat) as min_lat, min(lng) as min_lng, max(lat) as max_lat, max(lng) as max_lng")
      clusters = clusters.where("qtree_index like ?", "#{params[:id]}%" )
      clusters = clusters.group("substring(qtree_index, #{qtree.size + 1}, #{QTREE_GROUPS})")
      clusters = clusters.having("count(*) >= #{MIN_CLUSTER_SIZE}")


      clusters.all.each do |item|
        contents = contents.where("qtree_index not like ?", "#{item.qtree_group}%")
      end

      @content = contents.all
      clusters.all.each do |element|
        @content << {
            id: element.id,
            #type: 'group',
            lat: element.lat,
            lng: element.lng,
            #cost: element.point_count,
            #qtree_group: element.qtree_group,
            min_lat: element.min_lat.to_f,
            min_lng: element.min_lng.to_f,
            max_lat: element.max_lat.to_f,
            max_lng: element.max_lng.to_f
        }
      end
    else
      @content = contents.all
    end
  end
end
