class Page() {

	var dataCount = 0
	var _perPageSize = 0
	var _page = 1
	var startRecord = -1
	var endRecord = 0
	var pageCount = 0
	var previous = 0
	var next = 0
	var hasPrevious = false
	var hasNext = false

	def calculate():Unit = {
		if (_perPageSize >= 1 && dataCount >= 0){
			if (startRecord > -1 && dataCount>0) {
				endRecord = startRecord + _perPageSize + 1
				endRecord = if(endRecord > dataCount) dataCount + 1 else endRecord
			}
			
			
			if (dataCount == 0) {
				pageCount = 1
			} else
				pageCount = if(dataCount % _perPageSize == 0)  dataCount / _perPageSize 
						else dataCount / _perPageSize + 1

			_page = if(_page < 1) 1 else _page
			_page = if(_page > pageCount) pageCount else _page

			startRecord = _perPageSize * (_page - 1)
			startRecord = if(startRecord > dataCount) dataCount else startRecord

			endRecord = startRecord + _perPageSize + 1
			endRecord = if(endRecord > dataCount) dataCount + 1 else endRecord

			if (_page > 1)
				this.hasPrevious = true
			if (_page < pageCount)
				this.hasNext = true
		}
	}


	def previousPage():Int = {
		if (_page <= 1)
			previous = 1
		else
			previous = _page - 1
		previous
	}

	def nextPage():Int = {
		if (_page >= pageCount)
			next = pageCount
		else
			next = _page + 1
		next
	}

}
